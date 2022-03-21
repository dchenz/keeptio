# Keeptio

Keeptio is a small password manager project I've been working on in my spare time. Always been interested in encryption software and related tools, hence I made this using JavaFX. Still a work in progress.

## Current features

- Creating vaults (password, encryption settings)
- Encryption using AES-256 and various PBKDF2-HMAC methods
- CRUD operations on secrets
- Secrets can have their own passwords within the vault
- Creating groups to categorise secrets
- Moving secrets between groups

## Future features

- Backing up vaults and vault changes to cloud storage (can see some of it already in Vault Settings)
- Adding Twofish as a supported cipher alongside AES
- Adding key files alongside password authentication
- Changing vault password and encryption settings
- CRUD operations on groups
- Hierarchy of groups similar to a file system
- TOTP token generation for MFA (RFC 6238/4226)
- and other common features seen in password managers

## Demo

![1](./demo/1.jpg)
![2](./demo/2.jpg)
![3](./demo/3.jpg)
![4](./demo/4.jpg)

