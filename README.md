# 🔍 Memory Scanner & Writer

[![Language](https://img.shields.io/badge/Language-C%2B%2B-00599C?style=for-the-badge&logo=c%2B%2B)](https://isocpp.org/)
[![Platform](https://img.shields.io/badge/Platform-Windows%20(WinAPI)-0078D6?style=for-the-badge&logo=windows)](https://learn.microsoft.com/en-us/windows/win32/)
[![Category](https://img.shields.io/badge/Domain-Reverse%20Engineering-red?style=for-the-badge)](#)

Ferramenta desenvolvida em **C++** para varredura, análise e manipulação de memória virtual de processos em execução no sistema operacional Windows.

---

## 📌 Sobre o Projeto

O **Memory Scanner & Writer** demonstra conceitos avançados de sistemas operacionais e engenharia reversa no Windows. Utilizando a API nativa do Windows (Win32 API), a ferramenta permite inspecionar regiões de memória de processos alvos, buscar padrões de bytes/valores e realizar escrita em tempo de execução.

## 🚀 Funcionalidades

- 🎯 **Varredura de Memória (`VirtualQueryEx`):** Enumeração de páginas e regiões de memória válidas e acessíveis.
- 📖 **Leitura Segura (`ReadProcessMemory`):** Inspeção de valores inteiros, floats e arrays de bytes em endereços alvos.
- ✏️ **Manipulação de Dados (`WriteProcessMemory`):** Alteração de valores em memória em tempo real.
- 🔒 **Controle de Acesso:** Elevação de privilégios (`SeDebugPrivilege`) e abertura de handles com permissões granulares.

## 🛠️ Tecnologias Utilizadas

- **C++ (C++17 / C++20)**
- **Windows API (Win32)**: `kernel32.dll`, `psapi.lib`
- Compilador: MSVC / MinGW

## ⚙️ Como Compilar e Executar

### Pré-requisitos
- Visual Studio (com carga de trabalho C++) ou MinGW-w64.
- Windows 10/11 com privilégios de Administrador.

### Compilação (Visual Studio Developer Command Prompt)
```bash
cl.exe /EHsc /std:c++17 MemoryScannerWriter/*.cpp /Fe:MemoryScanner.exe
```

### Execução
> **Nota:** Execute o terminal como Administrador para permitir a abertura de handles de processos protegidos.
```bash
./MemoryScanner.exe
```

---

## 👤 Autor

Desenvolvido por **[Iury Almeida](https://github.com/IuryAlmeidaDev)**.
