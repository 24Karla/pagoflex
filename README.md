# PagoFlex

PagoFlex es una aplicación móvil desarrollada en Android con Jetpack Compose y SQLite, diseñada para la gestión integral de compromisos de pago, seguimiento financiero, roles de usuario (Usuario final, Agente y Ejecutivo), y notificaciones de vencimiento.

## Características Principales

- **Autenticación Segura y Roles**: Ingreso por credenciales con reconocimiento automático de rol y mensajes de error genéricos.
- **Registro Restringido**: Registro público exclusivamente para usuarios finales; roles administrativos gestionados por personal autorizado.
- **Validaciones Rigurosas**: Validación estricta de nombres (sin números ni símbolos) y seguridad de contraseñas de 5 requisitos (largo mínimo, mayúscula, minúscula, número y carácter especial).
- **Gestión de Comunas**: Selector de comunas respaldado por tabla relacional en SQLite.
- **Perfil de Usuario**: Modificación de datos permitida con bloqueo automático de información sensible/protegida (ID interno, RUT, rol).
- **Compromisos y Pagos**: Compromisos asociados a usuarios específicos y unificación del proceso de pagos.
- **Recursos Nativos**: Soporte de cámara y notificaciones push de vencimientos.

## Enlaces del Proyecto

- **Tablero Trello**: [Trello Board PagoFlex](https://trello.com/b/example/pagoflex)

## Arquitectura y Estructura

- **UI**: Jetpack Compose con Material 3 y animaciones de retroalimentación.
- **Persistencia**: SQLite Database (`UsuarioDbHelper`) con repositorio estructurado.
- **Navegación**: Jetpack Navigation Compose.

## Autor
Equipo PagoFlex - Duoc UC
