# CityFix 🏙️

> Aplicação Android para participação cívica e gestão de ocorrências urbanas num município.

Trabalho prático da unidade curricular **Arquiteturas Móveis** — Licenciatura em Engenharia Informática, ISEC (Instituto Politécnico de Coimbra), ano letivo 2026/27.

---

## 📱 Sobre o projeto

O **CityFix** permite aos munícipes **registar, georreferenciar e acompanhar a resolução de anomalias no espaço público**, por exemplo vias danificadas, roturas de água, recolha de resíduos ou falhas na iluminação pública.

A aplicação usa serviços na nuvem (Firebase) e funciona **offline**. Garante a rastreabilidade do ciclo de vida de cada ticket, valida as resoluções com imagem e vídeo e alerta por proximidade em tempo real os munícipes e as equipas de intervenção da autarquia.

## 👥 Perfis de utilizador

| Perfil | Acesso | Principais funcionalidades |
|---|---|---|
| **Munícipe Anónimo** | Sem autenticação, num raio de 10 km da Pr. da República | Dashboard (tempo médio de reparação, nº total de ocorrências, % de ocorrências ativas), lista e detalhe de ocorrências públicas sem dados pessoais do autor |
| **Munícipe Registado** | Autenticado (e-mail/password + MFA) | Registo validado por NIF, abrir ocorrências com fotos/vídeos, acompanhar as suas ocorrências, trocar mensagens com a autarquia, reabrir tickets, deteção de impactos pelo acelerómetro |
| **Operador Municipal** | Autenticado (e-mail/password + MFA) | Validar registos de munícipes, gerir as tipologias, painel de gestão operacional, controlar o ciclo de vida dos tickets, carregar provas da resolução |

## 🔄 Ciclo de vida de um ticket

```
Aberto ──► Pedido de Informações ◄──► Em Resolução ──► Resolvido ──(5 dias)──► Encerrado
                                                          │
                                                          └──► Reaberto (até 5 dias após a resolução)
```

- **Ocorrências de grupo:** quando há reports da mesma tipologia e situação num raio de 10 m, são agregados automaticamente.
- **Geofence de 20 m:** os autores são notificados quando a equipa camarária está perto da ocorrência.
- **Notificações:** mudanças de estado, mensagens, alertas de proximidade e aviso 24 h antes do fecho definitivo.

## 🛠️ Tecnologias

- **Linguagem:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Arquitetura:** MVVM, com separação entre componentes *stateless* e *stateful*
- **Backend:** Firebase
  - Authentication (com MFA)
  - Cloud Firestore (com persistência offline)
  - Cloud Storage (fotos e vídeos)
  - Cloud Messaging (notificações push)
- **Tarefas em segundo plano:** WorkManager (uploads condicionados à existência de rede)
- **Localização:** Fused Location Provider + Geofencing API
- **Sensores:** acelerómetro (deteção de impactos no pavimento)
- **Mapas:** _a definir_

## 📂 Estrutura do projeto

```
app/src/main/java/pt/isec/amov/cityfix/
├── data/
│   ├── model/          # Entidades (Occurrence, User, Category, Message, ...)
│   └── repository/     # Acesso ao Firebase e lógica de dados
├── ui/
│   ├── screens/        # Ecrãs: Screen (stateful) + Content (stateless)
│   ├── components/     # Composables reutilizáveis
│   ├── navigation/     # Grafo de navegação
│   └── theme/          # Cores, tipografia, temas
├── viewmodel/          # ViewModels por funcionalidade
├── sensors/            # Acelerómetro, localização, geofences
└── utils/              # Compressão de imagem, miniaturas, validações
```

## 🚀 Como executar

### Pré-requisitos
- Android Studio (versão recente)
- JDK 17+
- Um projeto Firebase configurado

### Configuração
1. Clonar o repositório:
   ```bash
   git clone https://github.com/<utilizador>/<repositorio>.git
   ```
2. Criar um projeto na [Consola do Firebase](https://console.firebase.google.com/) e registar a app Android com o package `pt.isec.amov.cityfix`.
3. Descarregar o ficheiro `google-services.json` e colocá-lo em `app/`.
4. Na consola, ativar **Authentication** (e-mail/password + MFA), **Firestore**, **Storage** e **Cloud Messaging**.
5. Abrir o projeto no Android Studio, sincronizar o Gradle e executar num dispositivo ou emulador.

## ✅ Requisitos gerais

- [ ] Idioma por omissão em inglês, com suporte para português
- [ ] Funcionamento correto em *portrait* e *landscape*
- [ ] Pedido e validação dinâmica de permissões (localização em primeiro e segundo plano, câmara, notificações)
- [ ] Compressão de imagens e geração de miniaturas antes do upload
- [ ] Mecanismos de acessibilidade
- [ ] Barras de topo, ícones e botões flutuantes que mudam consoante o contexto

## 📦 Entrega

- **Prazo:** 22/11/2026, 23:59
- **Defesa funcional:** de 23 a 27/11/2026
- **Formato:** ZIP `AMOV2627.<nr_aluno1>.<nr_aluno2>.zip`, sem `.gradle/`, `build/`, `app/build/` nem `local.properties`

Para gerar o ZIP só com os ficheiros versionados:
```bash
git archive --format=zip -o AMOV2627.<nr_aluno1>.<nr_aluno2>.zip HEAD
```

## 👨‍💻 Autores

| Nome | Nº de aluno |
|---|---|
| Leonardo Ferreira | _a preencher_ |
| _a preencher_ | _a preencher_ |

---

<sub>Projeto académico desenvolvido no âmbito da UC de Arquiteturas Móveis — ISEC, 2026/27.</sub>
