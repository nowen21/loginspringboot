# Login con Spring Boot y Spring Security

Este proyecto implementa un sistema robusto de autenticación y autorización utilizando **Spring Security**, una herramienta ampliamente adoptada por su flexibilidad, integración nativa con Spring Boot y capacidad para manejar autenticación basada en roles y permisos. En comparación con otras soluciones, destaca por su facilidad de personalización y su enfoque modular., basado en roles y permisos. Ofrece un control granular sobre las acciones que los usuarios pueden realizar, combinando roles para agrupar permisos y permisos individuales para un acceso más detallado.

## Incluye:
- Gestión de usuarios, roles y permisos.
- Validación de acceso basada en reglas de seguridad.
- Frontend con **Thymeleaf** para el manejo dinámico de permisos en la interfaz.
- Uso de **BCrypt** para la encriptación de contraseñas.
- Integración con **MySQL** como base de datos relacional.

### Ideal para:
Proyectos que requieren **seguridad avanzada** y **personalización** en la gestión de acceso.

## Implementación de la Integración con Spring Security y Base de Datos

### 1. Diseño del Modelo de Datos
Se definieron las siguientes tablas en la base de datos:

![Diagrama ER](ruta/diagrama-er.png)

Este diagrama ilustra las relaciones entre las tablas `usuarios`, `roles`, `permisos`, `usuarios_roles` y `roles_permisos`, destacando las conexiones y claves primarias/foráneas involucradas.

- **usuarios**: Almacena información de los usuarios como `username`, `password` y `enabled`.
- **roles**: Define roles como `ADMIN` o `USER`.
- **permisos**: Contiene acciones como `CREAR_USUARIO` o `EDITAR_REPORTE`.
- **usuarios_roles**: Relaciona usuarios con roles.
- **roles_permisos**: Relaciona roles con permisos.

### 2. Creación de Entidades JPA

#### Entidad Usuario
```java
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Authority> authorities;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_has_permissions", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_has_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        // Añadir los permisos directos del usuario
        this.permissions.forEach(grantedAuthorities::add);

        // Añadir los roles del usuario (prefijo ROLE_)
        /*
         * this.roles.forEach(rol ->
         * grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()))
         * );
         */

        // Añadir los permisos heredados de los roles
        this.roles.forEach(role -> grantedAuthorities.addAll(role.getPermissions()));

        return grantedAuthorities; 
    }

    /*
     * @Override
     * public Collection<? extends GrantedAuthority> getAuthorities() {
     * return authorities.stream()
     * .map(authority -> (GrantedAuthority) authority)
     * .collect(Collectors.toSet());
     * }
     */

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}

```

#### Entidad Role
```java
@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_has_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions;

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String getAuthority() {
        return nombre;
    }

```

#### Entidad Permission
```java
@Entity
@Table(name = "permisos")
public class Permission implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Override
    public String getAuthority() {
        return this.nombre;
    }
}
```

### 3. Servicio de Autenticación Personalizado

Se creó la clase `CustomUserDetailsService` para cargar los usuarios desde la base de datos, manejando casos especiales como usuarios deshabilitados o inexistentes. Si el usuario no se encuentra, lanza una excepción `UsernameNotFoundException`. Además, verifica el estado del usuario a través del campo `enabled` para garantizar que solo usuarios activos puedan iniciar sesión.

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Set<GrantedAuthority> authorities = new HashSet<>();

        usuario.getRoles().forEach(rol -> {
            authorities.add(rol);
            authorities.addAll(rol.getPermissions());
        });

        return new User(
            usuario.getUsername(),
            usuario.getPassword(),
            usuario.isEnabled(),
            true, true, true,
            authorities
        );
    }
}
```

### 4. Configuración de Spring Security
Se configuró una clase `SecurityConfig` para proteger rutas según roles y permisos. Además, incluye configuraciones avanzadas para proteger métodos mediante anotaciones, como `@PreAuthorize` y `@Secured`, permitiendo controlar el acceso a nivel de método:

```java
@PreAuthorize("hasAuthority('CREAR_USUARIO')")
public void crearUsuario() {
    // Lógica para crear usuario
}

@Secured("ROLE_ADMIN")
public void eliminarUsuario() {
    // Lógica para eliminar usuario
}
```


```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
            .antMatchers("/public/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin().loginPage("/login").permitAll()
            .and()
            .logout().permitAll();
    }
}
```

### 5. Encriptación de Contraseñas
Se utilizó **BCrypt** para garantizar la seguridad de las contraseñas almacenadas en la base de datos, proporcionando un alto nivel de protección contra ataques de fuerza bruta y diccionario. Este enfoque asegura que las contraseñas no se guarden en texto plano y sean resistentes a compromisos de datos.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### 6. Frontend con Thymeleaf
Se integró Thymeleaf para mostrar u ocultar elementos según los permisos y roles del usuario autenticado, utilizando expresiones de autorización que permiten una gestión dinámica de la interfaz basada en el nivel de acceso de cada usuario. Esto asegura que solo los elementos permitidos sean visibles para cada rol o permiso.

```html
<button th:if="${#authorization.expression('hasAuthority(\'CREAR_USUARIO\')')}" type="button">
    Crear Usuario
</button>
<p th:if="${#authorization.expression('hasRole(\'ADMIN\')')}">
    Bienvenido, Administrador.
</p>
```

---

## Tecnologías Utilizadas

- **Java 11**
- **Spring Boot 2.7+**
- **Spring Security**
- **Thymeleaf**
- **MySQL**

---

Este proyecto es ideal para aplicaciones que requieren un sistema de seguridad robusto y personalizable.

