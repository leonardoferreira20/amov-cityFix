# CityFix — Estado do projeto

Ponto de situação a 11/11/2026, sobre o commit `41ae99b` mais a atualização dos cartões da lista. Faltam 11 dias para a entrega (22/11/2026, 23:59).

Este documento junta o que já está implementado e o que falta para concluir o trabalho. O plano está em [CityFix_Plano.md](CityFix_Plano.md), as decisões em [CityFix_Decisoes.md](CityFix_Decisoes.md), as regras de negócio em [CityFix_RegrasNegocio.md](CityFix_RegrasNegocio.md) e as tarefas detalhadas em [Tarefas_Joao.md](Tarefas_Joao.md) e [Tarefas_Leonardo.md](Tarefas_Leonardo.md).

## 1. Resumo

| Parte | Feito | Falta |
|---|---|---|
| Fase 0 | 0.1 a 0.5 e 0.7; contrato da J7 na 0.6 | 0.6: restantes ações do operador |
| João | J1, J2, J3, J7 local, J8; J5 sem vídeo nem GPS; J4 sem mapa; base de multimédia | J6, J9, J10 anotadas |
| Leonardo | L3, L5, L6 (já restritas ao operador) | L1, L2, L4, L7 a L11 |
| Requisitos gerais | MVVM; textos em inglês e português; barra de topo dinâmica | Firebase, WorkManager, permissões, mapas, notificações, landscape e acessibilidade em todos os ecrãs |
| Entrega | — | Limpeza, relatório, ZIP |

## 2. O que está implementado

### 2.1 Fase 0
- **0.1 Modelo de dados** (`data/model`):
  - classes `User`, `Category`, `Incident`, `GeoLocation` (antes `GeoPoint`), `MediaAttachment`, `StatusChange`, `InfoRequest`, `Message` e `DashboardStats`;
  - enums `UserRole`, `AccountStatus`, `Situation`, `Visibility`, `IncidentStatus`, `MediaType`, `MediaPurpose`, `UploadStatus` e `InfoRequestStatus`;
  - coleções `IncidentsList`, `CategoriesList` e `UsersList`, como o `ContactsList` das aulas;
  - as classes guardadas têm `@Serializable`; o histórico de estados fica dentro da ocorrência (`history`).
- **0.2 Repositórios** (`data`): `IncidentRepository`, `CategoryRepository` e `UserRepository`, como o `ContactsRepository`.
  - Gravam em JSON no `filesDir`, guardam a lista em cache e têm dados de exemplo com ids coerentes entre si (`citizen-1`, `citizen-2`, `operator-1`, `cat-roads`, ...).
  - São criados na `CityFixApp`, a classe `Application` declarada no manifesto.
- **0.3 Regras de acesso** (`utils/AccessRules.kt`): funções Kotlin puras `isOperator`, `isCitizen`, `isAuthor`, `canViewIncident`, `canSeeAuthorData`, `canCreateIncident`, `canChangeStatus`, `canManageCategories`, `canValidateCitizens` e `canReopen` (até 5 dias depois de `resolvedAt`).
- **0.4 Sessão falsa:**
  - `SessionViewModel` (+ `Factory`) guarda o utilizador atual num `StateFlow<User?>`; `null` é o anónimo.
  - `selectProfile(role)` escolhe o primeiro utilizador ativo desse perfil no `UserRepository` (`citizen-1` ou `operator-1`).
  - `SessionSelector`: três `FilterChip` (Anónimo / Munícipe / Operador) no topo do ecrã de início.
  - O criador das categorias passou a ser o utilizador da sessão.
- **0.5 Navegação:**
  - Um só `NavHost` na `MainActivity`, dentro de um `Scaffold`, que arranca em `DashboardRoute`.
  - `MainTopAppBar` stateless: título conforme a rota, seta de voltar fora do início e ações conforme o perfil (expandir cartões para todos; Categorias e Validação só para o operador; Registo só para o anónimo).
  - As rotas do Leonardo (`CategoryListRoute`, `EditCategoryRoute`, `RegisterRoute`, `CitizenValidationRoute`) estão neste `NavHost`, sem alterações nos ecrãs.
  - Saíram o `Screen.JOAO` / `Screen.LEONARDO`, o `selectedScreen`, o `DevMenuScreen` e o `SAMPLE_OPERATOR_ID`.
- **0.7:** `values-pt/strings.xml` com todos os textos do Leonardo e do João; já não há textos fixos no código.
- Dependência `kotlinx-serialization-json` 1.11.0, a mesma das aulas.

### 2.2 Leonardo
- **L6 Categorias:** lista ordenada pelo nome; criar e editar no mesmo ecrã, com `EditCategoryRoute(categoryId: String? = null)`; ativar e desativar com um interruptor. `CategoriesList.getActiveSorted()` devolve só as ativas, para a J5.
  - Ficheiros: `CategoryListScreen`, `EditCategoryScreen`, `CategoryCard`, `CategoriesViewModel`, `EditCategoryViewModel`, `EditCategoryUiState`.
- **L3 Registo:** um só ecrã para munícipe e operador.
  - Erros campo a campo, NIF único por perfil e domínio `@aac-coimbra.pt` para operadores.
  - A deteção de impactos só aparece para o munícipe.
  - O munícipe fica `PENDING` e o operador `ACTIVE`, com um aviso no fim.
  - Ficheiros: `RegisterScreen`, `RegisterViewModel`, `RegisterUiState`, `utils/Validators`.
- **L5 Validação de munícipes:** lista dos pendentes com nome, NIF e georreferenciação, e botão para ativar.
  - Ficheiros: `CitizenValidationScreen` (com preview próprio), `PendingCitizenCard`, `CitizenValidationViewModel`.
- **Restrição ao operador (L5 e L6):** os botões de acesso só aparecem na barra de topo quando o perfil é Operador (`canManageCategories`, `canValidateCitizens`).
- As três tarefas foram testadas no emulador, têm os textos em inglês e português e descrições de acessibilidade nos botões e interruptores.

### 2.3 João
- **J1 Dashboard:** `IncidentsList.getDashboardStats()` calcula sobre todas as ocorrências, incluindo as privadas, o tempo médio de reparação (de `createdAt` a `closedAt`), o total e a percentagem de ativas. O `IncidentsViewModel` expõe-nos em `dashboardStats` e o `DashboardStatsCard` mostra-os no topo do `Dashboard`.
- **J2:**
  - Thumbnail da primeira foto no cartão (`IncidentPhoto`); sem foto, um ícone.
  - `IncidentCard` mostra sempre a categoria e a situação. Ao tocar, alterna entre recolhido e expandido. A expansão mostra o estado, a descrição e o botão «Ver detalhes».
  - O botão na `MainTopAppBar` expande ou recolhe todos os cartões da lista atual. A `MainActivity` guarda os IDs expandidos com `rememberSaveable`.
  - O `IncidentsViewModel` filtra a lista com `canViewIncident` conforme o utilizador da sessão.
  - O munícipe tem os chips "Todas" / "As minhas" (`IncidentFilter`, estado `onlyMine`).
  - «Ver detalhes» abre o detalhe (`IncidentDetailRoute(incidentId)`).
  - Compilação debug concluída. No Medium Phone, verificados o toque individual, a expansão e o recolher global, a abertura do detalhe pelo botão e a manutenção da expansão ao voltar.
- **J3 Botão "Nova ocorrência":** botão flutuante + no início, só para o munícipe (`canCreateIncident`).
- **J5 Criar ocorrência:**
  - `ReportIncidentScreen`, `ReportIncidentUiState` e `ReportIncidentViewModel` + `Factory`, no padrão do `EditScreen` / `EditContactUiState` das aulas.
  - Categoria (só as ativas) e situação em listas dropdown, descrição, visibilidade com `RadioButton` e foto obrigatória, com erros campo a campo.
  - Foto da galeria (Photo Picker) ou da câmara (`TakePicture` com `FileProvider`), guardada em `filesDir/images/` e registada como `MediaAttachment` (`IMAGE`, `REPORT`).
  - Localização provisória: a do registo do autor ou o centro do município (`utils/CityConstants.kt`).
  - Ao gravar, a lista e o dashboard atualizam-se.
  - Testado no emulador com uma foto da câmara.
- **Base de multimédia:** `utils/FileUtils`, `FileProvider` no manifesto, `res/xml/file_paths.xml`, dependência `coil-compose` 3.0.4 (a das aulas) e os componentes `IncidentPhoto` (mostra uma foto) e `PhotoPicker` (pré-visualização e botões Galeria / Câmara, usado na criação e na reabertura). Serve também para a L3, a L4 e a L7 do Leonardo.
- **J8 Reabertura:**
  - Botão "Reabrir" no detalhe, só para o autor, em Resolvido e dentro dos 5 dias (`canReopen`), com o prazo por baixo (`reopenDeadline`).
  - Justificação e foto obrigatórias num `AlertDialog`, com o `PhotoPicker` (galeria ou câmara).
  - `Incident.reopened()` (em `data/model/IncidentActions.kt`): passa a Aberto, apaga o `resolvedAt`, incrementa o `reopenCount`, junta a foto (`MediaPurpose.REOPEN`) e acrescenta um `StatusChange` com a justificação como nota.
  - O histórico mostra a nota de cada mudança e o detalhe mostra todas as fotos numa fila horizontal. A lista e o dashboard atualizam-se.
  - Testado no emulador com a "Água" e, com foto da câmara, com a "Iluminação" do `citizen-1`.
- **J4 Detalhe (falta o mapa):**
  - Foto da ocorrência no topo.
  - `IncidentDetailScreen` (stateless, como o `ShowScreen`), `IncidentDetailUiState` e `IncidentDetailViewModel` + `Factory`, criado por destino com o id da ocorrência e o utilizador da sessão.
  - Todos veem a categoria, a situação, o estado, a data e a descrição. O autor e o operador veem também as coordenadas e o histórico; só o operador vê o nome, o e-mail e o telefone do autor.
  - Uma ocorrência que o perfil não pode ver aparece como "não encontrada".
  - Espaço `operatorActions` para o `LifecycleActions` da L7, visível só para o operador.
  - Testado no emulador como anónimo, autor (`citizen-1`) e operador.
- **J7 Pedidos de informação:** secção partilhada pelo autor e pelo operador no detalhe. O operador pede em Aberto ou Em Resolução; a primeira resposta do autor repõe o estado anterior. O autor pede em qualquer estado exceto Encerrado, mantendo o estado. Os pedidos, as respostas em texto e as transições da J7 ficam gravados com a ocorrência no JSON local. As notificações ficam para a L11.
  - Compilação debug e teste no emulador concluídos. Verificados pedidos e respostas nos dois sentidos, reposição de Aberto após a resposta do autor, bloqueio de um segundo pedido do operador enquanto aguarda, histórico, detalhe público sem mensagens, bloqueio de ações em Encerrado e persistência após reiniciar a app.
- **Gráfico:** a segunda página do `Dashboard` mostra o número de ocorrências por categoria (`IncidentChart`, barras feitas com `Row` e `Box`).
- `EnumLabels.kt` liga cada `Situation` e `IncidentStatus` ao seu texto no `strings.xml`.
- **Design alinhado com os ecrãs do Leonardo (11/11):** sem cores de teste; títulos de secção em `titleMedium` alinhados à esquerda; cartões com elevação de 2 dp, `padding(16.dp)`, título em `titleMedium` e `onClickLabel` para a acessibilidade. As duas páginas do dashboard usam o mesmo `DashboardPage`.
- **Tema azul-petróleo (11/11):** `CityFixTheme` usa uma paleta fixa com variantes clara e escura. A `MainTopAppBar` partilhada usa o azul-petróleo do ícone com texto e ícones brancos. O `DashboardStatsCard` usa `primaryContainer` e dá mais peso aos valores.
  - Compilação debug concluída e ecrã inicial verificado no Medium Phone em modo claro, incluindo os ícones da barra de estado e os três indicadores.
- **Barra de topo:** títulos em seminegrito. O botão de expansão realça o ícone quando todos os cartões da lista estão expandidos e indica a ação disponível na descrição de acessibilidade em EN/PT.
- Testado no emulador com os dados de exemplo: 11,5 dias, 6 ocorrências e 67% ativas; o munícipe `citizen-1` tem 4 ocorrências suas em "As minhas", e o operador vê as 6.

### 2.4 Provisório (sai antes da entrega)
- `SessionSelector`: sai quando houver login (L1). O `SessionViewModel` fica; muda só a forma de preencher o `currentUser`.
- Repositórios em JSON: são trocados pelo Firestore quando o Firebase entrar.

### 2.5 Documentação
- `Padroes_Aulas.md`: ContactsApp de 6/11 (multimédia de fotos) e diferenças em relação às aulas.
- `CityFix_Decisoes.md`: nomes do modelo, um repositório por entidade, validações do registo, formato das coordenadas, sessão falsa, navegação por perfil e decisão 16 fechada.
- `CityFix_Plano.md`: multimédia de fotos marcada como dada, vídeo por definir e estado da Fase 0.

## 3. O que falta implementar

### 3.1 Fase 0 (os dois)
- [ ] **0.6** Fechar os parâmetros das restantes ações do `LifecycleActions`. O contrato do componente de mensagens está na secção 7 do plano.

### 3.2 João
- [ ] J4 Mapa (aula #4).
- [ ] J5 Localização por GPS quando se tira a foto (aula #4) e vídeo (por definir).
- [ ] J6 Agrupamento a 10 m (aula #4).
- [ ] J9 Restrição dos 10 km para anónimos (aula #4).
- [ ] J10 Deteção de impactos (sensores, ainda sem aula).

### 3.3 Leonardo
- [ ] L8 Multimédia: a base já existe (feita com a J5); falta usá-la no registo (L3), no perfil (L4) e na prova de resolução (L7). O vídeo está por definir.
- [ ] L4 Perfil: fechar antes o conflito 11.
- [ ] L7 `LifecycleActions`: depende da 0.6. O espaço no detalhe (J4) já existe.
- [ ] L1 Login e aviso de conta pendente (Firebase). Substitui o `SessionSelector`.
- [ ] L2 MFA e recuperação de password (Firebase).
- [ ] L9 Compressão, thumbnail e envio com WorkManager.
- [ ] L10 Geofence de 20 m.
- [ ] L11 Notificações, encerramento aos 5 dias e aviso das 24 h.
- Pendente nas tarefas já feitas:
  - [ ] L3: password e criação da conta (L1), foto opcional (L8) e georreferenciação (aula #4, ponto 8).

### 3.4 Requisitos gerais do enunciado

| Requisito | Estado |
|---|---|
| Kotlin, Jetpack Compose, MVVM com ecrãs stateful e stateless | Seguido no código novo |
| Firebase Authentication (e-mail/password, MFA, recuperação por e-mail) | Por fazer (L1, L2) |
| Cloud Firestore com cache offline | Por fazer; substitui os repositórios JSON |
| Firebase Storage e WorkManager, com envio só quando há rede | Por fazer (L9) |
| Imagens comprimidas e com thumbnail | Por fazer (L9) |
| Cloud Messaging e notificações | Por fazer (L11) |
| Permissões em runtime: localização (1.º e 2.º plano), câmara, notificações | Por fazer (aula #4, L8, L10, L11) |
| Inglês por omissão e português | Feito em todos os ecrãs |
| Portrait e landscape | Por verificar em todos os ecrãs (layouts adaptativos: aula #4) |
| Barras de topo, ícones e botões flutuantes dinâmicos | `MainTopAppBar` com título e ações por ecrã e perfil; botões flutuantes nos ecrãs do Leonardo |
| Acessibilidade | Descrições nos botões da barra de topo e nos ecrãs do Leonardo; falta rever o resto |

### 3.5 Decisões por fechar
O detalhe está em [CityFix_Decisoes.md](CityFix_Decisoes.md), secções 2 e 3.
- Conflitos com o enunciado: 11 (nome editável e morada) e 15 (o tempo médio inclui os 5 dias até ao encerramento). O 16 ficou fechado.
- Por confirmar, do Leonardo: 7 (falta só a unicidade do e-mail, que o Firebase resolve), 8, 10, 13, 28, 29, 30 e 32.
- Por confirmar, do João: 17, 31, A e B.

### 3.6 Problemas conhecidos e limpeza
- [x] `MainScreen`: blocos de cor e textos de teste retirados (11/11).
- [x] Dados do preview da `MainActivity` trocados por ocorrências de exemplo realistas (11/11).
- [ ] Os enums estão na pasta `data/model/enums/` mas declaram o package `data.model`.
- [ ] O `gradlew` está no Git sem permissão de execução (`100644`); corrige-se com `git update-index --chmod=+x gradlew`.
- [ ] Quando entrar o Firestore, as classes do modelo precisam de valores por omissão em todos os campos, porque o `toObject()` exige um construtor sem argumentos.
- A câmara usa o contrato `TakePicture`, que não exige a permissão `CAMERA` enquanto a app não a declarar no manifesto. O pedido de permissões em runtime que o enunciado exige fica para a aula #4.
- Os testes no emulador alteram os dados guardados em JSON. Os dados de exemplo só são lidos quando o ficheiro ainda não existe; para os repor: `adb shell pm clear pt.isec.amov.cityfix`.
- Dados de exemplo para testar a reabertura (J8), todos do `citizen-1`: "Iluminação" resolvida há 1 dia (mostra o botão "Reabrir") e "Saneamento" encerrada (não o mostra), ambas com histórico de estados. A "Água" também está resolvida, há 2 dias.
- Os campos de texto seguem o padrão das aulas (`StateFlow`). Com escrita muito rápida num emulador lento perderam-se letras; a escrever normalmente funcionou.
- O stash `stash@{0}` (alterações antigas do João, de 11/11) já não é preciso: o `Rules.kt` deu origem ao `AccessRules.kt` e o `IncidentChart` foi recuperado.

### 3.7 Entrega
- [ ] Retirar o que é provisório (secção 2.4).
- [ ] Testar todos os ecrãs em portrait e landscape, em inglês e em português.
- [ ] Relatório técnico em PDF: soluções técnicas, opções de implementação concretizadas e funcionais, oportunidades de melhoria.
- [ ] ZIP `AMOV2627.2024126292.2024113113`, sem `.gradle`, `build`, `app/build` nem `local.properties` (por exemplo com `git archive`, como no README).
- [ ] Entregar no Nónio até 22/11/2026, 23:59. A defesa funcional é de 23 a 27/11, com os dois presentes.

## 4. Calendário que falta (proposta)

| Quando | O quê |
|---|---|
| Até 16/11 | Fechar a 0.6 e avançar com L8, L4 e L7 |
| Depois de cada aula | Aula #4: mapa da J4, localização da J5, J6, J9, georreferenciação da L3 e F1. Firebase: L1, L2, L11 e troca dos repositórios. WorkManager: L9. Sensores: J10. Geofencing: L10 |
| 17/11 a 22/11 | Integração, limpeza, testes em landscape e em PT/EN, relatório e ZIP |
