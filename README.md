# Login con Spring Boot y Spring Security

Este proyecto implementa un sistema robusto de autenticación y autorización utilizando **Spring Security**, una herramienta ampliamente adoptada por su flexibilidad, integración nativa con Spring Boot y capacidad para manejar autenticación basada en roles y permisos. En comparación con otras soluciones, destaca por su facilidad de personalización y su enfoque modular., basado en roles y permisos. Ofrece un control granular sobre las acciones que los usuarios pueden realizar, combinando roles para agrupar permisos y permisos individuales para un acceso más detallado.

## Incluye:
- Gestión de usuarios, roles y permisos, permitiendo asignar múltiples roles a un usuario y definir permisos específicos para cada acción dentro del sistema.
- Validación de acceso basada en reglas de seguridad, incluyendo protección de rutas y métodos mediante configuraciones avanzadas de Spring Security como @PreAuthorize y @Secured, garantizando un control preciso del acceso.
- Frontend con Thymeleaf para el manejo dinámico de permisos en la interfaz, permitiendo adaptar la visualización de elementos de forma personalizada según los roles y permisos del usuario autenticado.
- Uso de BCrypt para la encriptación de contraseñas, garantizando seguridad avanzada mediante hashing, lo que dificulta ataques de fuerza bruta y asegura que las contraseñas no se almacenen en texto plano.
- Integración con MySQL como base de datos relacional, utilizando Spring Data JPA para interactuar con las entidades y gestionar transacciones de forma eficiente. Esta configuración asegura un rendimiento óptimo y facilita la portabilidad hacia otros sistemas de bases de datos relacionales si es necesario.

### Ideal para:
Proyectos que requieren seguridad avanzada, personalización en la gestión de acceso y un control granular adaptado a diferentes niveles de autorización.

## Implementación de la Integración con Spring Security y Base de Datos

### 1. Diseño del Modelo de Datos
Se definieron las siguientes tablas en la base de datos:

![Diagrama ER](ruta/diagrama-er.png)

Este diagrama ilustra las relaciones entre las tablas `usuarios`, `roles`, `permisos`, `usuarios_roles` y `roles_permisos`, destacando las conexiones y claves primarias/foráneas involucradas.

- **users**: Almacena información de los usuarios como `username`, `password` y `enabled`.
- **roles**: Define roles como `ADMIN` o `USER`.
- **permissions**: Contiene acciones como `CREAR_USUARIO` o `EDITAR_REPORTE`.
- **user_has_permissions**: Relaciona usuarios con permisos.
- **user_has_roles**: Relaciona usuarios con roles.
- **role_has_permissions**: Relaciona roles con permisos.

### 2. Creación de Entidades JPA

#### Entidad User
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
@Table(name = "permissions")
public class Permission  implements GrantedAuthority{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

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

    @Override
    public String getAuthority() {
       return nombre;
    }

    @Override
    public String toString() {
        return "Permission [id=" + id + ", nombre=" + nombre + "]";
    }
}
```

### 3. Servicio de Autenticación Personalizado

Se creó la clase `CustomUserDetailsService` para cargar los usuarios desde la base de datos, manejando casos especiales como usuarios deshabilitados o inexistentes. Si el usuario no se encuentra, lanza una excepción `UsernameNotFoundException`. Además, verifica el estado del usuario a través del campo `enabled` para garantizar que solo usuarios activos puedan iniciar sesión.

```java
@Service
public class UserService implements UserDetailsService {
    private static final Logger logger =  LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    // Constructor-based injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

     @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });
        return user;
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
public class SecurityConfig {
    private final UserService userService; // Nuestro servicio personalizado

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true) // Redirige al usuario a /home después del login
                        .permitAll())
                .logout(logout -> logout.permitAll());
        return http.build();
    }

  

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
<button sec:authorize="hasAuthority('PERMISO_USUARIO')" type="button">
    Crear Usuario
</button>
<p sec:authorize="hasAuthority('PERMISO_USUARIO')">
    Bienvenido, Administrador.
</p>
```

---

## Tecnologías Utilizadas

- **Java 21**
- **Spring Boot 3.4.1+**
- **Spring Security**
- **Thymeleaf**
- **Maven**
- **MySQL**

---

Este proyecto es ideal para aplicaciones que requieren un sistema de seguridad robusto y personalizable.

