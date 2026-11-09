# CityFix — Estado do projeto

Ponto de situação a 9/11/2026, no commit `5c955c9`. Faltam 13 dias para a entrega (22/11/2026, 23:59).

Este documento junta o que já está implementado e o que falta para concluir o trabalho. O plano está em [CityFix_Plano.md](CityFix_Plano.md), as decisões em [CityFix_Decisoes.md](CityFix_Decisoes.md) e as tarefas detalhadas em [Tarefas_Joao.md](Tarefas_Joao.md) e [Tarefas_Leonardo.md](Tarefas_Leonardo.md).

## 1. Resumo

| Parte | Feito | Falta |
|---|---|---|
| Fase 0 | 0.1, 0.2 e parte da 0.7 | 0.3, 0.4, 0.5, 0.6 e o resto da 0.7 |
| João | Lista e cartões de ocorrências, dashboard provisório | J1 a J10 |
| Leonardo | L3, L5, L6 | L1, L2, L4, L7 a L11 |
| Requisitos gerais | MVVM; textos em inglês e português nos ecrãs do Leonardo | Firebase, WorkManager, permissões, mapas, notificações, landscape e acessibilidade em todos os ecrãs |
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
  - O ecrã de início (`MainScreen`) recebe a lista do `IncidentsViewModel`.
- Dependência `kotlinx-serialization-json` 1.11.0, a mesma das aulas.
- **0.7 (parcial):** `values-pt/strings.xml` existe, com os textos dos ecrãs do Leonardo.

### 2.2 Leonardo
- **L6 Categorias:** lista ordenada pelo nome; criar e editar no mesmo ecrã, com `EditCategoryRoute(categoryId: String? = null)`; ativar e desativar com um interruptor. `CategoriesList.getActiveSorted()` devolve só as ativas, para a J5.
  - Ficheiros: `CategoryListScreen`, `EditCategoryScreen`, `CategoryCard`, `CategoriesViewModel`, `EditCategoryViewModel`, `EditCategoryUiState`.
- **L3 Registo:** um só ecrã para munícipe e operador.
  - Erros campo a campo, NIF único por perfil e domínio `@aac-coimbra.pt` para operadores.
  - A deteção de impactos só aparece para o munícipe.
  - O munícipe fica `PENDING` e o operador `ACTIVE`, com um aviso no fim.
  - Ficheiros: `RegisterScreen`, `RegisterViewModel`, `RegisterUiState`, `utils/Validators`.
- **L5 Validação de munícipes:** lista dos pendentes com nome, NIF e georreferenciação, e botão para ativar.
  - Ficheiros: `CitizenValidationScreen`, `PendingCitizenCard`, `CitizenValidationViewModel`.
- As três tarefas foram testadas no emulador, têm os textos em inglês e português e descrições de acessibilidade nos botões e interruptores.

### 2.3 João (até ao commit `08b9a3c`)
- `IncidentCard` e `IncidentList`: lista com cartões expansíveis.
- `Dashboard`: duas páginas lado a lado (`LazyRow`), ainda provisório.
- `MainScreen`: estrutura provisória com blocos coloridos.
- Seletor `Screen.JOAO` / `Screen.LEONARDO` na `MainActivity` e ícone da app.

### 2.4 Provisório (sai antes da entrega)
- `selectedScreen` na `MainActivity`: escolhe o ecrã da app e o da preview.
- `DevMenuScreen`: menu no ecrã LEONARDO com acesso às categorias, ao registo e à validação de munícipes.
- `NavHost` só no ramo LEONARDO, com as rotas de `ui/navigation/Destinations.kt`.
- `SAMPLE_OPERATOR_ID = "operator-1"`: operador que fica como criador das categorias enquanto não há sessão.
- Repositórios em JSON: são trocados pelo Firestore quando o Firebase entrar.

### 2.5 Documentação
- `Padroes_Aulas.md`: atualizado com o ContactsApp de 6/11 (multimédia de fotos) e com as diferenças em relação às aulas.
- `CityFix_Decisoes.md`: nomes do modelo, um repositório por entidade, validações do registo, formato das coordenadas e modelo de dados alinhado com o código.
- `CityFix_Plano.md`: multimédia de fotos marcada como dada, vídeo por definir e nota sobre o menu provisório.

## 3. O que falta implementar

### 3.1 Fase 0 (os dois)
- [ ] **0.3** `AccessRules.kt` com as regras do que cada perfil vê e faz. Fecha a restrição "só para o operador" da L5 e da L6.
- [ ] **0.4** Sessão falsa com seletor Anónimo / Munícipe / Operador. Substitui o `selectedScreen`, o `DevMenuScreen` e o `SAMPLE_OPERATOR_ID`.
- [ ] **0.5** Um só `NavHost` na `MainActivity`, dentro de um `Scaffold` com `MainTopAppBar` (título, seta de voltar e ações por ecrã). As rotas das categorias, do registo e da validação já estão em `Destinations.kt` e passam para este `NavHost`.
- [ ] **0.6** Assinaturas do `LifecycleActions` e do componente de mensagens, na secção 7 do plano.
- [ ] **0.7** Passar para `strings.xml`, em inglês e português, os textos dos ecrãs do João.

### 3.2 João
- [ ] J1 Dashboard com os 3 números (a classe `DashboardStats` já existe).
- [ ] J2 Lista com cartões expansíveis e filtro por perfil.
- [ ] J3 Botão "Nova ocorrência".
- [ ] J4 Detalhe da ocorrência (o mapa depende da aula #4).
- [ ] J5 Criar ocorrência, usando `CategoriesList.getActiveSorted()`, o que fecha também o "Feito quando" da L6. A foto já foi dada nas aulas; o vídeo está por definir; a localização depende da aula #4.
- [ ] J7 Pedidos de informação e mensagens.
- [ ] J8 Reabertura.
- [ ] J6 Agrupamento a 10 m (aula #4).
- [ ] J9 Restrição dos 10 km para anónimos (aula #4).
- [ ] J10 Deteção de impactos (sensores, ainda sem aula).

### 3.3 Leonardo
- [ ] L8 Multimédia: galeria, câmara e Coil, já dados nas aulas; o vídeo está por definir.
- [ ] L4 Perfil: fechar antes o conflito 11.
- [ ] L7 `LifecycleActions`: depende da 0.6 e da J4.
- [ ] L1 Login e aviso de conta pendente (Firebase).
- [ ] L2 MFA e recuperação de password (Firebase).
- [ ] L9 Compressão, thumbnail e envio com WorkManager.
- [ ] L10 Geofence de 20 m.
- [ ] L11 Notificações, encerramento aos 5 dias e aviso das 24 h.
- Pendente nas tarefas já feitas:
  - [ ] L3: password e criação da conta (L1), foto opcional (L8) e georreferenciação (aula #4, ponto 8).
  - [ ] L5 e L6: restrição ao operador (0.3 e 0.4).

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
| Inglês por omissão e português | Feito nos ecrãs do Leonardo; falta nos do João |
| Portrait e landscape | Por verificar em todos os ecrãs (layouts adaptativos: aula #4) |
| Barras de topo, ícones e botões flutuantes dinâmicos | Botões flutuantes nos ecrãs do Leonardo; falta a `MainTopAppBar` (0.5) |
| Acessibilidade | Descrições nos ecrãs do Leonardo; falta nos do João |

### 3.5 Decisões por fechar
O detalhe está em [CityFix_Decisoes.md](CityFix_Decisoes.md), secções 2 e 3.
- Conflitos com o enunciado: 11 (nome editável e morada), 16 (ocorrências privadas na listagem pública) e 15 (o tempo médio inclui os 5 dias até ao encerramento).
- Por confirmar, do Leonardo: 7 (falta só a unicidade do e-mail, que o Firebase resolve), 8, 10, 13, 28, 29, 30 e 32.
- Por confirmar, do João: 17, 31, A, B, C, D e E.

### 3.6 Problemas conhecidos e limpeza
- [ ] Textos fixos no código: `MainScreen.kt` (3), `Dashboard.kt` (2), `IncidentList.kt` (1) e `IncidentCard.kt` (2).
- [ ] `IncidentCard`: as etiquetas "Email:" e "Tel:" mostram o estado e a descrição; a altura fixa de 120 dp corta o conteúdo quando o cartão expande.
- [ ] Cores de teste (`Color.Blue`, `Red`, `Green` e `Yellow`) em `MainScreen.kt` e `Dashboard.kt`.
- [ ] Textos impróprios na preview do João e em `MainScreen.kt`: limpar antes da entrega e da defesa.
- [ ] Os enums estão na pasta `data/model/enums/` mas declaram o package `data.model`.
- [ ] O `gradlew` está no Git sem permissão de execução (`100644`); corrige-se com `git update-index --chmod=+x gradlew`.
- [ ] Quando entrar o Firestore, as classes do modelo precisam de valores por omissão em todos os campos, porque o `toObject()` exige um construtor sem argumentos.
- Os campos de texto seguem o padrão das aulas (`StateFlow`). Com escrita muito rápida num emulador lento perderam-se letras; a escrever normalmente funcionou.

### 3.7 Entrega
- [ ] Retirar o que é provisório (secção 2.4).
- [ ] Testar todos os ecrãs em portrait e landscape, em inglês e em português.
- [ ] Relatório técnico em PDF: soluções técnicas, opções de implementação concretizadas e funcionais, oportunidades de melhoria.
- [ ] ZIP `AMOV2627.2024126292.2024113113`, sem `.gradle`, `build`, `app/build` nem `local.properties` (por exemplo com `git archive`, como no README).
- [ ] Entregar no Nónio até 22/11/2026, 23:59. A defesa funcional é de 23 a 27/11, com os dois presentes.

## 4. Calendário que falta (proposta)

| Quando | O quê |
|---|---|
| Até 16/11 | Fase 0 em falta (0.3 a 0.7), J1 a J5, J7, J8, L8, L4 e L7 |
| Depois de cada aula | Aula #4: mapa da J4, localização da J5, J6, J9, georreferenciação da L3 e F1. Firebase: L1, L2, L11 e troca dos repositórios. WorkManager: L9. Sensores: J10. Geofencing: L10 |
| 17/11 a 22/11 | Integração, limpeza, testes em landscape e em PT/EN, relatório e ZIP |
