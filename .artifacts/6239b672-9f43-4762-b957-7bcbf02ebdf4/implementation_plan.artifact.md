# Implementation Plan - PagoFlex Feature Refinement & Security Update

This plan outlines the implementation of all requested improvements for PagoFlex, focusing on access control, credential-based login, restricted registration, robust password/input validation, comuna database table and dropdown, profile management, password recovery/change, user-specific commitments, payment integration, native permissions, repository separation, and documentation.

## User Review Required

> [!IMPORTANT]
> - **Database Schema Changes**: Adding a `comunas` table and updating user/commitment/payment foreign keys / structures.
> - **Authentication Flow**: Removing the pre-login role selector and authenticating strictly via email and password, determining the role from the database record.
> - **Role Authorization**: Restricting self-registration to "Usuario final" only. Executives/Agents must be created by authorized administrative roles.

## Open Questions

- None currently; all requirements are clearly specified.

## Proposed Changes

### Database & Repository Layer
#### [NEW] [Repository classes and Comuna database support](file:///C:/Users/orian/OneDrive/Desktop/pagoflex/app/src/main/java/com/example/pagoflex/data)
- Create `comunas` table in SQLite DB with predefined Chilean comunas.
- Refactor database helpers / create a Repository layer to separate persistence logic from ViewModels and Screens.
- Update User and Commitment entities to tie commitments to specific users and integrate payments with commitments.

### Authentication & Security
#### [MODIFY] [Login & Registration Screens/ViewModels](file:///C:/Users/orian/OneDrive/Desktop/pagoflex/app/src/main/java/com/example/pagoflex/ui/screens)
- Remove role selector from login.
- Implement generic error message ("Correo o contraseña incorrectos").
- Restrict registration to "Usuario final" only.
- Implement 5-rule password security (length >= 8, uppercase, lowercase, number, special character).
- Add strict input validations (e.g., names accept no numbers/symbols).

### Features & Navigation
#### [MODIFY] [Navigation & Screens](file:///C:/Users/orian/OneDrive/Desktop/pagoflex/app/src/main/java/com/example/pagoflex/navigation)
- Update profile screen to allow editing appropriate fields while locking immutable fields (ID, RUT, role).
- Integrate password change and recovery flows.
- Update commitment screens to display user-specific commitments without automatic generation.
- Unify "Registrar Pago" with commitment payment.
- Ensure camera permission is in `AndroidManifest.xml` and notification reminders are functioning.
- Add feedback animations and remove unused code/duplicate folders.

### Documentation
#### [NEW] [README.md & Trello Reference](file:///C:/Users/orian/OneDrive/Desktop/pagoflex/README.md)
- Complete README.md with project overview, architecture, setup, and Trello board link.

## Verification Plan

### Automated Tests
- Run unit and instrumentation tests via Gradle.

### Manual Verification
- Deploy to device/emulator and test login, registration, password validation, comuna dropdown, profile editing, and payment integration.
