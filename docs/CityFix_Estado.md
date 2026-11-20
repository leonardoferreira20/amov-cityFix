# CityFix — Estado do projeto

Ponto de situação a 20/11/2026, sobre o commit `3181bc9`. Faltam 2 dias para a entrega (22/11/2026, 23:59). A 20/11 o Leonardo ficou com as tarefas que faltavam do João.

Este documento junta o que já está implementado e o que falta para concluir o trabalho. O plano está em [CityFix_Plano.md](CityFix_Plano.md), as decisões em [CityFix_Decisoes.md](CityFix_Decisoes.md), as regras de negócio em [CityFix_RegrasNegocio.md](CityFix_RegrasNegocio.md) e as tarefas detalhadas em [Tarefas_Joao.md](Tarefas_Joao.md) e [Tarefas_Leonardo.md](Tarefas_Leonardo.md).

## 1. Resumo

| Parte | Feito | Falta |
|---|---|---|
| Fase 0 | Concluída (0.1 a 0.7) | — |
| João | J1 a J9, com GPS, mapa, agrupamento a 10 m e restrição dos 10 km; base de multimédia; passagem dos dados para o Firestore | Passou o que faltava para o Leonardo a 20/11 |
| Leonardo | L1 a L7, L9, L11 (publicada), F1 nos seus ecrãs e J10 (recebida do João) | Teste das notificações em dois dispositivos |
| Requisitos gerais | MVVM; Firebase (Authentication, Firestore com cache, Storage e Cloud Messaging com Cloud Functions); WorkManager; compressão e thumbnails; permissões em runtime; inglês e português; landscape e acessibilidade revistos em todos os ecrãs | Testes em dispositivos reais |
| Entrega | — | Relatório, ZIP e entrega no Nónio |

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
  - `MainTopAppBar` stateless: título conforme a rota, seta de voltar fora do início e ações conforme o perfil (expandir cartões para todos; Categorias e Validação só para o operador; Registo só para o anónimo; Perfil para o munícipe e o operador).
  - As rotas do Leonardo (`CategoryListRoute`, `EditCategoryRoute`, `RegisterRoute`, `CitizenValidationRoute`) estão neste `NavHost`, sem alterações nos ecrãs.
  - Saíram o `Screen.JOAO` / `Screen.LEONARDO`, o `selectedScreen`, o `DevMenuScreen` e o `SAMPLE_OPERATOR_ID`.
- **0.6 Contratos:** `InformationRequests` (J7) e `LifecycleActions` (L7), descritos na secção 7 do plano.
- **0.7:** `values-pt/strings.xml` com todos os textos do Leonardo e do João; já não há textos fixos no código.
- Dependência `kotlinx-serialization-json` 1.11.0, a mesma das aulas.

### 2.2 Leonardo
- **Vídeo na nova ocorrência e na prova de resolução (20/11, recebido do João, documentação oficial):** fecha os pontos 13 e A.
  - `VideoPicker`: grava com a câmara (`CaptureVideo`, com o limite de 30 s e 50 MB) ou escolhe da galeria (`PickVisualMedia` só com vídeos, até 50 MB). Mostra a pré-visualização e deixa remover o vídeo. Está na «Nova ocorrência» e no diálogo de resolução.
  - O vídeo é guardado em `videos/` no armazenamento da app (novo caminho no `FileProvider`), entra na ocorrência como `MediaAttachment` do tipo `VIDEO` e é enviado pelo `MediaUploadWorker` sem compressão.
  - `IncidentVideo`: `VideoView` num `AndroidView`, com um botão de reproduzir e pausar e um aviso quando o vídeo não está disponível. O detalhe mostra-o por baixo das fotos.
  - O `storage.rules` de produção passou a aceitar vídeos até 50 MB.
  - Testado no emulador a 20/11 com um vídeo de teste da galeria: pré-visualização, envio com sucesso e reprodução no detalhe a partir do endereço do Storage. Falta testar a gravação com a câmara e o vídeo na prova de resolução (precisa da conta de operador).
- **J10 Deteção de impactos (20/11, recebida do João, documentação oficial):** fecha o ponto 31.
  - `ImpactDetectionService`: serviço em primeiro plano, como o de proximidade, para o munícipe com a deteção ativa no perfil (`runsImpactDetection`). Lê o acelerómetro (`SENSOR_DELAY_GAME`) e a localização de 5 em 5 s, e funciona com a app fechada. A `MainActivity` arranca-o e para-o conforme a sessão, a opção do perfil e a permissão de localização.
  - `ImpactDetector`: o filtro passa-baixo da documentação oficial estima a gravidade, e a aceleração vertical é a projeção do resto na direção da gravidade. Há impacto a partir de 6 m/s², com o munícipe a mais de 10 km/h, no máximo uma vez por minuto. Tem 6 testes unitários.
  - A sugestão é uma notificação no canal «Sugestões de ocorrências». Tocar nela abre a «Nova ocorrência» com «Vias Públicas» (`PAVEMENT_CATEGORY_ID`), «Dano» e o ponto do impacto marcado no mapa; a foto continua obrigatória.
  - Testado no emulador a 20/11: sugestão com a app em segundo plano, formulário pré-preenchido, nenhuma sugestão com o munícipe parado, formulário mantido ao rodar o ecrã e serviço parado ao desligar a deteção no perfil.
- **L6 Categorias:** lista ordenada pelo nome; criar e editar no mesmo ecrã, com `EditCategoryRoute(categoryId: String? = null)`; ativar e desativar com um interruptor. `CategoriesList.getActiveSorted()` devolve só as ativas, para a J5.
  - Ficheiros: `CategoryListScreen`, `EditCategoryScreen`, `CategoryCard`, `CategoriesViewModel`, `EditCategoryViewModel`, `EditCategoryUiState`.
  - Gravar ignora um segundo toque enquanto grava (`isSaving`), como no perfil. Antes, dois toques seguidos no ✓ criavam a categoria duas vezes e voltavam até ao início. Reproduzido e corrigido no emulador a 12/11/2026.
- **L3 Registo:** um só ecrã para munícipe e operador.
  - Erros campo a campo, NIF único por perfil e domínio `@aac-coimbra.pt` para operadores.
  - A deteção de impactos só aparece para o munícipe.
  - O munícipe fica `PENDING` e o operador `ACTIVE`, com um aviso no fim.
  - Foto opcional (L8): avatar circular (`UserAvatar`) com as iniciais do nome e os botões Galeria / Câmara (`PhotoSourceButtons`) no topo; fica em `User.picturePath`. A foto é apagada se for trocada ou se o registo for abandonado (`RegisterViewModel.onCleared`). Testado no emulador: iniciais, foto da galeria, apagamento ao sair sem registar e caminho gravado ao registar.
  - Ficheiros: `RegisterScreen`, `RegisterViewModel`, `RegisterUiState`, `utils/Validators`, `UserAvatar`.
- **L4 Perfil:** botão de conta na barra de topo, só para o munícipe e o operador (`canEditProfile`). Fecha o conflito 11 a favor do enunciado.
  - Editáveis: foto (avatar com Galeria / Câmara), telefone (validado com o `Validators.phoneError`) e, no munícipe, a Deteção de Impactos.
  - A secção «Dados da conta» apresenta o nome, o e-mail, o NIF e, no munícipe, a georreferenciação como texto só de leitura. Em «Editar perfil» altera-se o telefone, troca-se ou remove-se a foto e, no munícipe, muda-se a deteção de impactos. Não há morada: o `address` saiu do `User`.
  - Ao gravar, volta ao início e o utilizador da sessão é atualizado (`SessionViewModel.refreshCurrentUser`).
  - Foto: a nova só substitui a antiga ao gravar, e a antiga é apagada nesse momento; se se sair sem gravar, a nova é apagada.
  - O registo e o perfil partilham os componentes `FormTextField`, `UserPhotoField` e `ImpactDetectionSwitch` (`ui/components/UserFormFields.kt`). As coordenadas escrevem-se com `GeoLocation.toDisplayText()` (`utils/LocationFormat.kt`), que a validação de munícipes também usa.
  - Ficheiros: `ProfileScreen` (com preview), `ProfileViewModel`, `ProfileUiState`, `ProfileViewModelFactory`.
  - Testado no emulador a 12/11/2026, como munícipe e como operador: o anónimo não tem o botão; um telefone inválido mostra o erro e não grava; o telefone, a Deteção de Impactos e a foto ficam no JSON e voltam a aparecer ao reabrir; a foto nova é apagada ao sair sem gravar e a antiga ao gravar outra; o operador não vê a georreferenciação nem a Deteção de Impactos; em landscape o ecrã faz scroll. O registo continua igual depois de passar para os componentes partilhados.
- **L5 Validação de munícipes:** lista dos pendentes com nome, NIF e georreferenciação, e botão para ativar.
  - Ficheiros: `CitizenValidationScreen` (com preview próprio), `PendingCitizenCard`, `CitizenValidationViewModel`.
- **L7 Ciclo de vida do ticket (operador):** secção «Gestão do estado» no detalhe, com o componente `LifecycleActions`.
  - «Iniciar trabalhos», em Aberto: passa a Em resolução e preenche `workStartedAt`.
  - «Marcar como resolvida», em Em resolução: diálogo com foto comprovativa obrigatória (galeria ou câmara, com o `PhotoPicker`) e nota opcional. Passa a Resolvido, preenche `resolvedAt` e junta a foto com o propósito `RESOLUTION_PROOF`.
  - Em Pedido de informações, Resolvido e Encerrado não há ações; aparece o motivo (à espera da resposta do autor, data do encerramento automático ou ocorrência encerrada).
  - Encerramento automático: ao carregar as ocorrências, as resolvidas há mais de 5 dias passam a Encerradas (`IncidentRepository.loadIncidents`).
  - Regras em `AccessRules` (`canStartWork`, `canResolve`, `isClosingDue`) e transições em `IncidentActions` (`workStarted`, `resolved`, `closed`).
  - Testado no emulador como operador: Aberto → Em resolução → Resolvido com foto da galeria e nota, histórico e data de encerramento; e o "Saneamento" dos dados de exemplo, resolvido há 22 dias, encerrado ao arrancar.
- **L11 Notificações (17/11, documentação oficial):** Cloud Functions com o Cloud Messaging; só avisos do sistema (ponto 32).
  - App: ao iniciar sessão, o token do Cloud Messaging fica em `deviceTokens/<uid>` (`DeviceTokenRepository`) e os operadores subscrevem o tópico `operators`; ao terminar sessão, o token é apagado (`deleteToken`). O `CityFixMessagingService` recebe mensagens só com dados e mostra a notificação com os textos do `strings.xml` (`IncidentNotifications`), no canal «Atualizações das ocorrências». Tocar na notificação abre o detalhe da ocorrência.
  - Servidor (`functions/index.js`): `notifyIncidentChanges` corre quando uma ocorrência muda e avisa o autor das mudanças de estado feitas por outra pessoa, dos pedidos de informação e das respostas do operador e dos alertas de proximidade; avisa o operador que fez o pedido quando o autor responde; e avisa todos os operadores (tópico) quando o autor faz um pedido. `closeResolvedIncidents` corre de hora a hora: avisa o autor 24 h antes do fim do prazo (marca `closingWarningSentAt`) e encerra as ocorrências resolvidas há mais de 5 dias, mesmo sem ninguém abrir a app.
  - `firebase.json` e `.firebaserc` permitem publicar as funções e as regras com o Firebase CLI. As regras do Firestore ganharam a coleção `deviceTokens`, em que cada utilizador só mexe no seu token.
  - Publicado a 17/11 com o Firebase CLI (Node.js 22): as regras do Firestore, a `notifyIncidentChanges` em `europe-southwest1`, a região do Firestore, e a `closeResolvedIncidents` em `europe-west1`, porque o Cloud Scheduler não existe em Madrid. As imagens das funções são apagadas ao fim de 1 dia. A `closeResolvedIncidents` que sobrou em `europe-southwest1` da primeira tentativa foi apagada na consola (confirmado a 20/11).
  - Testado a 17/11 com dois emuladores (operador num, munícipe no outro): o operador ficou a menos de 20 m da «Iluminação» e o munícipe recebeu a notificação de proximidade com a app em segundo plano. A `notifyIncidentChanges` também correu quando o operador respondeu a um pedido e pediu informação, mas o segundo emulador foi abaixo por falta de memória (o Mac não aguenta dois emuladores com o Android 17) e não recebeu essas notificações. Falta testar os outros tipos e o toque que abre o detalhe em dois dispositivos reais. A `closeResolvedIncidents` corre de hora a hora, sem erros nos registos (confirmado a 20/11).
- **F1 Landscape e acessibilidade nos ecrãs do Leonardo (17/11):**
  - Registo e perfil com duas colunas em landscape, no padrão do João (`Row` com duas colunas, cada uma com o seu scroll); em portrait continuam numa coluna. No registo: à esquerda a foto, os dados e a palavra-passe, à direita o perfil, o mapa, a deteção de impactos e «Registar». No perfil: à esquerda «Editar perfil», à direita «Dados da conta», a verificação em dois passos e «Terminar sessão».
  - Categorias e validação de munícipes numa grelha que se adapta à largura (`GridCells.Adaptive(320.dp)`): uma coluna em portrait, duas em landscape.
  - Acessibilidade: títulos de secção marcados como cabeçalhos (`heading()`), rótulo e valor dos dados da conta lidos juntos (`mergeDescendants`) e erros do registo e do login anunciados quando aparecem (`liveRegion`).
  - Previews em landscape no registo e no perfil.
- **L9 Fotos no Storage (17/11, documentação oficial):** só as fotos das ocorrências (criação, reabertura e prova de resolução).
  - Ao gravar uma ocorrência, o `IncidentRepository` pede ao `MediaUploadScheduler` um trabalho do WorkManager por cada foto por enviar que exista neste telemóvel. O trabalho só corre com rede (`NetworkType.CONNECTED`) e tenta de novo até 5 vezes.
  - O `MediaUploadWorker` comprime a foto (1280 px, JPEG a 80%) e cria o thumbnail (320 px, JPEG a 70%) com o `ImageCompressor`, que também acerta a rotação EXIF. Depois envia os dois para `incidents/<id>/` no Storage e guarda os endereços na ocorrência, numa transação do Firestore (`markMediaUploaded`).
  - As listas mostram o thumbnail e o detalhe a foto do Storage. Enquanto o envio não acaba, aparece a foto local e o aviso «Há fotos que ainda não foram enviadas».
  - O Storage fica em modo de teste, por indicação do professor (17/11): durante 30 dias, qualquer pessoa lê e escreve. O `storage.rules` fica como versão de produção: só utilizadores autenticados leem e escrevem, só JPEG até 5 MB, e o anónimo vê as fotos pelo endereço de download.
  - Compilado. O projeto já está no Blaze e o Storage foi ativado em modo de teste (17/11).
  - Testado no emulador a 17/11 com um munícipe: ao gravar a ocorrência, o `MediaUploadWorker` correu e acabou com sucesso em cerca de 13 s. No Storage ficaram a foto (55 KB) e o thumbnail (320 px, 16 KB) em `incidents/<id>/`. No Firestore, a foto passou a `UPLOADED`, com `remoteUrl` e `thumbnailUrl`, e o cartão do início mostra o thumbnail vindo do Storage. Com a conta de operador, a foto também aparece (confirmado pelo Leonardo).
- **Restrição ao operador (L5 e L6):** os botões de acesso só aparecem na barra de topo quando o perfil é Operador (`canManageCategories`, `canValidateCitizens`).
- Todas as tarefas do Leonardo foram testadas no emulador, têm os textos em inglês e português e descrições de acessibilidade nos botões e interruptores.

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
  - O munícipe escolhe o local com um toque no mapa e vê as coordenadas no formulário. O ponto é obrigatório e é independente da foto (decisão 12, revista a 16/11).
  - Ao gravar, a lista e o dashboard atualizam-se.
  - Testado no emulador a 16/11: submeter sem marcar um ponto mostrou o erro. Tocar no mapa apresentou as coordenadas, que se mantiveram ao juntar uma foto da câmara. O rascunho foi abandonado antes de gravar.
- **Base de multimédia:** `utils/FileUtils`, `FileProvider` no manifesto, `res/xml/file_paths.xml`, dependência `coil-compose` 3.0.4 (a das aulas) e os componentes `IncidentPhoto` (mostra uma foto) e `PhotoPicker` (pré-visualização e botões Galeria / Câmara, usado na criação e na reabertura). Serve também para a L3, a L4 e a L7 do Leonardo.
- **J8 Reabertura:**
  - Botão "Reabrir" no detalhe, só para o autor, em Resolvido e dentro dos 5 dias (`canReopen`), com o prazo por baixo (`reopenDeadline`).
  - Justificação e foto obrigatórias num `AlertDialog`, com o `PhotoPicker` (galeria ou câmara).
  - `Incident.reopened()` (em `data/model/IncidentActions.kt`): passa a Aberto, apaga o `resolvedAt`, incrementa o `reopenCount`, junta a foto (`MediaPurpose.REOPEN`) e acrescenta um `StatusChange` com a justificação como nota.
  - O histórico mostra a nota de cada mudança e o detalhe mostra todas as fotos numa fila horizontal. A lista e o dashboard atualizam-se.
  - Testado no emulador com a "Água" e, com foto da câmara, com a "Iluminação" do `citizen-1`.
- **J4 Detalhe:**
  - Foto da ocorrência no topo.
  - `IncidentDetailScreen` (stateless, como o `ShowScreen`), `IncidentDetailUiState` e `IncidentDetailViewModel` + `Factory`, criado por destino com o id da ocorrência e o utilizador da sessão.
  - Todos veem a categoria, a situação, o estado, a data e a descrição. O autor e o operador veem também as coordenadas e o histórico; só o operador vê o nome, o e-mail e o telefone do autor.
  - Uma ocorrência que o perfil não pode ver aparece como "não encontrada".
  - Espaço `operatorActions` para o `LifecycleActions` da L7, visível só para o operador.
  - Testado no emulador como anónimo, autor (`citizen-1`) e operador.
- **J7 Pedidos de informação:** secção partilhada pelo autor e pelo operador no detalhe. O operador pede em Aberto ou Em Resolução; a primeira resposta do autor repõe o estado anterior. O autor pede em qualquer estado exceto Encerrado, mantendo o estado. Os pedidos, as respostas em texto e as transições da J7 ficam gravados com a ocorrência no JSON local. As notificações ficam para a L11.
  - Compilação debug e teste no emulador concluídos. Verificados pedidos e respostas nos dois sentidos, reposição de Aberto após a resposta do autor, bloqueio de um segundo pedido do operador enquanto aguarda, histórico, detalhe público sem mensagens, bloqueio de ações em Encerrado e persistência após reiniciar a app.
- **Gráficos no início:** a secção «Estatísticas» mostra os gráficos por estado e por categoria juntos. Os dois contam as ocorrências visíveis na lista, incluindo o filtro «As minhas». Em portrait, o resumo, os gráficos e a lista partilham o scroll vertical e há um atalho para a lista. Em landscape, os indicadores e os gráficos ficam ao lado da lista.
- `EnumLabels.kt` liga cada `Situation` e `IncidentStatus` ao seu texto no `strings.xml`.
- **Design alinhado com os ecrãs do Leonardo:** títulos de secção alinhados à esquerda, cartões com elevação de 2 dp e `onClickLabel` para a acessibilidade. Os cartões das ocorrências mostram o estado e uma seta que acompanha a expansão.
- **Tema azul-petróleo (11/11):** `CityFixTheme` usa uma paleta fixa com variantes clara e escura. A `MainTopAppBar` partilhada usa o azul-petróleo do ícone com texto e ícones brancos. O `DashboardStatsCard` usa `primaryContainer` e dá mais peso aos valores.
  - Compilação debug concluída e ecrã inicial verificado no Medium Phone em modo claro, incluindo os ícones da barra de estado e os três indicadores.
- **Barra de topo e sessão:** títulos em seminegrito e botão «Login» com texto e ícone para o anónimo. O botão «Terminar sessão» fica no topo do início dos utilizadores autenticados e também no perfil. O botão de expansão só aparece quando há ocorrências visíveis. O botão flutuante do munícipe identifica a ação «Nova ocorrência».
- A 16/11, o `Medium_Phone` confirmou os dois gráficos em simultâneo nos perfis anónimo e munícipe, o fim da sessão pelo início, o filtro «As minhas», o atalho para a lista, a expansão, a validação do telefone, a disposição em colunas em landscape e o acesso anónimo limitado aos 10 km. A versão debug, os testes unitários existentes e o lint passaram.
- Testado no emulador com os dados de exemplo: 11,5 dias, 6 ocorrências e 67% ativas; o munícipe `citizen-1` tem 4 ocorrências suas em "As minhas", e o operador vê as 6.

### 2.4 Aula #4 e correções (16/11/2026)
- **Base de localização** (ficha P10): `maps-compose` 6.1.0 e `play-services-location` 21.3.0; `utils/LocationUtils` (`hasLocationPermission`, `locationUpdates` com o `FusedLocationProviderClient`, `distanceMeters` com `Location.distanceBetween`); `rememberDeviceLocation` (`produceState`, como o `ContactDistanceSection` das aulas).
- **Permissões em runtime:** a `MainActivity` pede a localização ao arrancar (`RequestMultiplePermissions`, como na P10). O botão Câmara pede a permissão `CAMERA` antes de abrir a câmara e mostra um aviso se for recusada.
- **Chave do Google Maps:** escrita no `AndroidManifest.xml`, como nas aulas, e partilhada pelos dois; o `google-services.json` também está no Git (decisão de 16/11). Não é preciso configurar nada para compilar.
- **J5 localização:** o formulário usa o `MapLocationPicker` com o centro inicial em Coimbra. O toque define o ponto e sem ponto o formulário mostra `error_location_required`. A seleção manual funciona sem a permissão de localização do dispositivo.
- **J6 Agrupamento:** ao gravar, `IncidentsList.findGroupPrimary` procura a principal mais antiga, não encerrada, da mesma categoria, a menos de 10 m; a nova fica com o `primaryIncidentId` dela. O detalhe mostra «Ocorrência de grupo: N participações neste local».
- **J4 Mapa:** `IncidentMap` em todos os detalhes. O autor e o operador veem o pino com zoom 17 e as coordenadas; os restantes veem o pino com zoom 11 (cidade), sem coordenadas e sem gestos de zoom ou deslocação (decisão 17).
- **J9 Restrição dos 10 km:** `AccessRules.anonymousAccess` devolve `ALLOWED`, `LOCATING`, `NO_PERMISSION` ou `OUT_OF_AREA`. Fora do raio, sem permissão ou enquanto localiza, o anónimo não vê o dashboard nem a lista; vê uma mensagem e o seletor de perfil (que faz de login até à L1).
- **L3 Georreferenciação:** o munícipe escolhe a localização num `MapLocationPicker` (como o da P10, sem geocoder) ou com «Usar a minha localização atual». É obrigatória para o munícipe e fica em `User.location`.
- **Landscape** (padrão da P10, `LocalConfiguration` + `Row` com `weight`): início (seletor, filtro e números à esquerda; lista à direita), detalhe (dados à esquerda; fotos e mapa à direita) e nova ocorrência (foto e localização à esquerda; formulário à direita).
- **Correções da análise de 16/11:**
  - dados de exemplo repostos: o "Semáforo" (Iluminação) está Resolvido há 1 dia e serve para testar a reabertura; o "Saneamento" está Encerrado, com a última mudança feita por `system`;
  - lint sem erros: datas formatadas fora dos composables (`utils/formatDateTime`) e `app_name` com `translatable="false"`;
  - a foto de uma nova ocorrência abandonada é apagada (`ReportIncidentViewModel.onCleared`);
  - erros de gravação tratados na criação, no início dos trabalhos, na resolução e na reabertura (`error_save_failed`), sem deixar a cache alterada;
  - o perfil volta a permitir gravar se o utilizador não for encontrado (`isSaving`);
  - os enums passaram de `data/model/enums/` para `data/model/`, a pasta do seu pacote;
  - o `gradlew` ficou com permissão de execução no Git.
- **Resto da app Contactos (`Contacts_#3`), 16/11, segunda parte:**
  - **Mapa de ocorrências** (`IncidentsMapScreen`, como o `ContactsMapScreen`): botão de mapa na barra de topo para todos os perfis com acesso. Mostra as ocorrências que o perfil vê na lista; tocar no balão do pino abre o detalhe. Se o perfil vê a localização exata de todas, a câmara ajusta-se aos pinos com `LatLngBounds` depois de o mapa ter dimensões. Alterações ao conteúdo das ocorrências que mantenham os pinos nas mesmas coordenadas preservam o zoom. Nos restantes casos, o mapa fica com zoom de cidade e sem gestos.
  - A 16/11, no `Medium_Phone`, o mapa ficou aberto após 14 toques no botão de afastar e após rodar para landscape. O teste instrumentado confirmou que a pinça de dois dedos alterou o zoom e manteve o ecrã do mapa aberto.
  - **Distância e «Navegar»** para o operador no detalhe (`IncidentDirections`, como o `ContactDistanceSection`): distância em km até à ocorrência e botão que abre o trajeto no Google Maps (`LocationUtils.openDirections`).
  - **Serviço de proximidade** (`services/ProximityService`, como o da P10): serviço em primeiro plano do tipo `location`, arrancado pela `MainActivity` enquanto a sessão é de operador e parado nos outros perfis. A cada posição, procura ocorrências ativas (desde 20/11 todas menos Encerrado, incluindo Em resolução, como pede o II.3) a menos de 20 m e grava um `ProximityAlert` na ocorrência. O autor e o operador veem os alertas no detalhe («Equipa camarária na zona»). A notificação ao autor entra com o Firebase.
  - **Permissões:** as notificações (`POST_NOTIFICATIONS`, Android 13+) são pedidas com a localização ao arrancar, como na P10. A localização em 2.º plano é pedida ao operador e ao munícipe com a deteção de impactos ativa, com um diálogo a explicar antes (`BackgroundLocationDialog`); o Android abre as definições para escolher «Permitir sempre».
  - Testado no emulador: mapa do anónimo afastado e mapa do operador ajustado aos 7 pinos; pedido de notificações; diálogo e definições da localização em 2.º plano; serviço em primeiro plano ativo como operador e parado como munícipe; alerta gravado nas duas ocorrências em Aberto a 1 m e não na que está Em resolução; distância, «Navegar» e secção do alerta no detalhe do operador.
- Testado no emulador a 16/11/2026: pedido de localização ao arrancar; anónimo em Coimbra com lista e em Lisboa bloqueado; mapa exato do autor e mapa afastado do anónimo; detalhe e início em landscape; pedido da permissão da câmara; nova ocorrência com foto da câmara a 2 m da "Vias Públicas", ligada a ela como grupo de 2; mapa do registo com o pino no toque.

### 2.4b Firebase: Firestore e Authentication (16/11/2026, segundo a documentação oficial)
- **Firestore com cache offline:** os três repositórios deixaram o JSON e passaram para as coleções `users`, `categories` e `incidents`. A cache persistente fica ligada na `CityFixApp` (`persistentCacheSettings`). As leituras usam `get().await()`; as gravações usam `set()` sem esperar, para funcionarem offline (o Firestore aplica-as logo na cache e envia-as quando houver rede).
- A lista do início ouve o Firestore em tempo real (`addSnapshotListener` em `IncidentRepository.observeIncidents`), por isso já não é preciso `refreshIncidents()`.
- Os modelos têm valores por omissão em todos os campos, como pede o `toObject()`.
- Dados iniciais: só as categorias. São criadas quando o primeiro operador inicia sessão e a coleção está vazia (`CategoryRepository.seedIfEmpty`). Utilizadores e ocorrências criam-se a usar a app.
- **Authentication (L1, L2):** `AuthRepository` com login por e-mail e password, registo, recuperação da password por e-mail, verificação do e-mail e MFA com TOTP. A sessão fica guardada pelo Firebase e é retomada ao abrir a app (`SessionViewModel`).
  - Ecrã de login (`LoginScreen`, `LoginViewModel`): erros campo a campo, «Esqueceu-se da palavra-passe?», «Criar conta», código do segundo fator num diálogo e aviso de conta pendente (a sessão é terminada logo).
  - Registo: password e confirmação; cria a conta no Authentication, verifica o NIF no Firestore (se já existir, apaga a conta acabada de criar), grava o utilizador com o uid e envia o e-mail de verificação. Depois termina a sessão e pede para entrar.
  - Perfil: «Verificação em dois passos» (envio e confirmação do e-mail, chave TOTP e botão para a abrir na app autenticadora, código de confirmação) e «Terminar sessão».
  - Saíram o `SessionSelector` e os utilizadores de exemplo. O anónimo tem o botão de login na barra de topo e no ecrã de acesso restrito.
- **Regras de segurança:** `firestore.rules` na raiz do repositório, para publicar na consola.
- Compilado e lint sem erros. Regras publicadas a 16/11 e confirmadas pela API REST (categorias e ocorrências legíveis sem sessão; utilizadores não).
- Testado no emulador com o Firebase a 16/11/2026, com as contas de [Contas_Teste.md](Contas_Teste.md): registo do operador (conta ativa); primeiro login do operador, que criou as 5 categorias; palavra-passe errada ("E-mail ou palavra-passe errados"); registo do munícipe com a localização atual (pendente); login do munícipe pendente (aviso e sessão terminada); validação pelo operador; login do munícipe ativo; nova ocorrência com foto da câmara, gravada no Firestore e mostrada no dashboard; recuperação da palavra-passe ("E-mail enviado"); terminar sessão.
- Por testar: a verificação em dois passos. Exige um e-mail que receba a verificação e o TOTP ativo no projeto (comando no Cloud Shell), por isso tem de ser feita com uma conta real.

### 2.4c Localização desligada no telemóvel (20/11/2026)
- Problema: depois de autorizar a localização, se a localização do telemóvel estivesse desligada, a app não a ligava e o anónimo ficava em «A verificar a sua localização…» (o `FusedLocationProviderClient` não devolve posições com a localização desligada).
- Correção: `LocationUtils.checkLocationSettings` usa o `SettingsClient` dos Google Play Services e, se a localização estiver desligada, mostra o diálogo do sistema para a ligar (`ResolvableApiException` + `StartIntentSenderForResult`). A `MainActivity` pede-o sempre que a localização faz falta: anónimo no início, registo, operador (alerta dos 20 m) e munícipe com deteção de impactos. Ao voltar à app (`LifecycleEventEffect(ON_RESUME)`), a permissão e o estado da localização são lidos de novo.
- O anónimo com a localização desligada vê «A localização está desligada» e o botão «Ativar localização» (`AnonymousAccess.LOCATION_OFF`).
- Compilado, lint sem erros e testes unitários a passar. Testado no emulador a 20/11: com a permissão dada e a localização desligada, a app mostra ao abrir o diálogo do sistema para a ligar e, depois de ligada, passa ao dashboard.

### 2.4d Ocorrências só dentro de Coimbra (20/11/2026)
- O mapa da nova ocorrência mostra o círculo de 10 km à volta da Praça da República e abre mais afastado para o mostrar. Um toque fora do círculo mostra «Escolha um ponto a menos de 10 km do centro da cidade…» a vermelho por baixo do mapa, e a ocorrência não é aceite ao submeter. A mesma verificação vale para o ponto sugerido pela deteção de impactos.
- `AccessRules.isInsideCity` serve ao acesso anónimo e às ocorrências; o raio passou a `CITY_RADIUS_METERS`.
- Testado no emulador a 20/11: diálogo do sistema para ligar a localização ao abrir a app com ela desligada e a permissão dada (2.4c); círculo no mapa, erro com um ponto fora, recusa ao submeter e erro a desaparecer com um ponto dentro.

### 2.4e Texto maior (20/11/2026)
- Botão na barra de topo, em todos os ecrãs e para todos os perfis: mostra **A+** com o texto maior ativo (fundo branco e letra azul-petróleo) e **A−** com o texto normal, como pedido pelo João.
- Ativo, o `CityFixTheme` troca o `LocalDensity` por um com `fontScale` × 1,2. Como multiplica o tamanho de letra do Android, respeita-o; só o texto (`sp`) aumenta, os `dp` ficam iguais. Os diálogos também ficam com o texto maior.
- A escolha fica no `SettingsViewModel` (ao nível da Activity, por isso sobrevive à navegação e à rotação) e no `SettingsRepository` (`SharedPreferences`, por isso sobrevive a fechar a app).
- TalkBack: o botão é um `IconToggleButton` com a descrição «Texto maior» e o estado «Ativado»/«Desativado» (`stateDescription`); o texto «A+»/«A−» é retirado da leitura (`clearAndSetSemantics`).
- O título da barra de topo passou a ter uma linha com reticências, para não empurrar os botões.
- Testado no emulador a 20/11 com o Android a 1,3× e o texto maior (1,56× no total): início, estatísticas, cartões (recolhidos e expandidos), detalhe, diálogo de pedido de informação, perfil, login, registo e barra de topo do operador (6 ações) e do anónimo (com «Login»), em portrait; perfil e início em landscape; a escolha mantém-se ao rodar e ao reabrir a app. Não houve texto cortado nem sobreposições a corrigir.
- Limitações: o mapa (Google Maps) e as notificações do sistema não são afetados; o anúncio do TalkBack foi confirmado pela árvore de acessibilidade, não com o TalkBack a falar.

### 2.5 Provisório (sai antes da entrega)
- ~~`SessionSelector`~~: saiu a 16/11 com o login (L1).
- ~~Repositórios em JSON~~: trocados pelo Firestore a 16/11.

### 2.6 Documentação
- `Padroes_Aulas.md`: ContactsApp de 6/11 (multimédia de fotos) e diferenças em relação às aulas.
- `CityFix_Decisoes.md`: nomes do modelo, um repositório por entidade, validações do registo, formato das coordenadas, sessão falsa, navegação por perfil, decisão 16 fechada e conflito 11 fechado (perfil, 12/11).
- `CityFix_Plano.md`: multimédia de fotos marcada como dada, vídeo por definir e estado da Fase 0.

## 3. O que falta implementar

### 3.1 Fase 0 (os dois)
- [x] **0.6** Contratos fechados: `InformationRequests` (J7) e `LifecycleActions` (L7).

### 3.2 João
- [x] J4 Mapa (16/11).
- [x] J5 Escolha obrigatória do ponto no mapa pelo munícipe (16/11). O vídeo foi feito pelo Leonardo a 20/11.
- [x] J6 Agrupamento a 10 m (16/11).
- [x] J9 Restrição dos 10 km para anónimos (16/11). Quando houver login (L1), o anónimo bloqueado passa a ver o ecrã de login.
- [x] J10 Deteção de impactos: passou para o Leonardo a 20/11, com o vídeo da J5, a revisão final dos ecrãs do João e os pontos 17, 31, A e B. Feita e testada pelo Leonardo a 20/11 (secção 2.2).

### 3.3 Leonardo
- [x] J10 Deteção de impactos (recebida do João): feita e testada a 20/11.
- [x] Vídeo na nova ocorrência (J5, recebido do João) e na prova de resolução: feito a 20/11.
- [x] Revisão final dos ecrãs do João (20/11): no emulador, em portrait e landscape e em inglês e português, o início, a lista, o detalhe, a nova ocorrência e o mapa estão corretos. Corrigido o detalhe em landscape: a coluna da direita (fotos, vídeo e mapa) passou a deslocar-se e o mapa tem 200 dp, como em portrait; antes, com o vídeo, o mapa deixava de aparecer. Títulos de secção marcados como cabeçalhos e previews novos no cartão, nos pedidos de informação e no mapa.
- [x] L8 Multimédia: as fotos do registo (L3), do perfil (L4) e da prova de resolução (L7) usam a base feita com a J5, e desde 20/11 há vídeo opcional na nova ocorrência e na resolução.
- [x] L4 Perfil (12/11), com o conflito 11 fechado.
- [x] L7 `LifecycleActions` (11/11). O vídeo comprovativo opcional foi feito a 20/11 e a notificação ao autor com a L11.
- [x] L1 Login e aviso de conta pendente (16/11).
- [x] L2 MFA com TOTP e recuperação de password (16/11).
- [x] L9 Compressão, thumbnail e envio para o Storage com WorkManager (17/11). Storage em modo de teste; envio testado no emulador e foto vista pelo operador.
- [x] L10 Alerta de 20 m: a deteção e o registo do alerta estão feitos (serviço de proximidade, 16/11). A notificação ao autor foi feita com a L11 e testada entre dois emuladores a 17/11. A 20/11 o alerta passou a valer para todas as ativas, incluindo Em resolução (II.3), e o ponto 30 ficou fechado (qualquer operador).
- [ ] L11 Notificações: implementada e publicada a 17/11 (app, Cloud Functions e regras do Firestore). A proximidade foi testada entre dois emuladores; falta testar os outros tipos em dois dispositivos reais, com o João.
- Pendente nas tarefas já feitas:
  - [x] L3: password e criação da conta no Authentication (16/11).

### 3.4 Requisitos gerais do enunciado

- O projeto `cityfix-3dbb1` foi criado no plano Spark e tem o método de e-mail e password ativo. O Gradle usa o plugin Google Services para ler `app/google-services.json` da aplicação `pt.isec.amov.cityfix` e inclui o SDK de Firebase Authentication. A versão debug compilou e o Firebase inicializou ao abrir a app no emulador. Falta ligar o login e o registo ao serviço (L1).

| Requisito | Estado |
|---|---|
| Kotlin, Jetpack Compose, MVVM com ecrãs stateful e stateless | Seguido no código novo |
| Firebase Authentication (e-mail/password, MFA, recuperação por e-mail) | Feito a 16/11; falta testar com as regras publicadas e o TOTP ativo no projeto |
| Cloud Firestore com cache offline | Feito a 16/11 (cache persistente, gravações offline, lista em tempo real) |
| Firebase Storage e WorkManager, com envio só quando há rede | Fotos feitas a 17/11 (L9) e vídeos a 20/11; Storage em modo de teste; envio testado no emulador |
| Imagens comprimidas e com thumbnail | Implementado a 17/11 (L9): 1280 px a 80% e thumbnail de 320 px a 70%, antes do envio |
| Cloud Messaging e notificações | Implementado e publicado a 17/11 (L11): Cloud Functions enviam mensagens do Cloud Messaging e a app mostra-as; proximidade testada, os outros tipos por testar em dois dispositivos |
| Permissões em runtime: localização (1.º e 2.º plano), câmara, notificações | Feitas a 16/11: localização e notificações ao arrancar, câmara antes de a abrir, 2.º plano para o operador e para a deteção de impactos |
| Inglês por omissão e português | Feito em todos os ecrãs |
| Portrait e landscape | Layouts adaptativos no início, no detalhe e na nova ocorrência (16/11). Nos ecrãs do Leonardo (17/11): duas colunas no registo e no perfil e grelha nas categorias e na validação; o login e a edição de categoria ficam numa coluna. Revisão final de todos os ecrãs a 20/11, com o detalhe em landscape corrigido |
| Barras de topo, ícones e botões flutuantes dinâmicos | `MainTopAppBar` com título e ações por ecrã e perfil; botões flutuantes nos ecrãs do Leonardo |
| Acessibilidade | Botão de texto maior (A+/A−) na barra de topo (20/11). Descrições nos botões da barra de topo e nos ecrãs do Leonardo. Nos ecrãs do Leonardo (17/11): títulos de secção marcados como cabeçalhos, rótulo e valor dos dados da conta lidos juntos e erros anunciados quando aparecem. A 20/11, os títulos de secção dos ecrãs do João também passaram a cabeçalhos; os ícones sem descrição são todos decorativos |

### 3.5 Decisões por fechar
O detalhe está em [CityFix_Decisoes.md](CityFix_Decisoes.md), secções 2 e 3.
- Conflitos com o enunciado: nenhum por fechar. Ficaram fechados o 11 (12/11, a favor do enunciado), o 16 e o 15 (20/11: o tempo médio inclui os 5 dias até ao encerramento).
- Por confirmar: nenhum. O 13 e o A (vídeo) ficaram fechados a 20/11. Ficaram fechados o 7 (o Firebase não aceita e-mails repetidos e o NIF é único por perfil), o 8, o 10 e o 29 (16/11, revisto a 20/11), o 28 e o 32 (17/11, com a L11), o 31 (20/11, com a J10) e o 17, o 30 e o B (20/11).

### 3.6 Problemas conhecidos e limpeza
- [x] `MainScreen`: blocos de cor e textos de teste retirados (11/11).
- [x] Dados do preview da `MainActivity` trocados por ocorrências de exemplo realistas (11/11).
- [x] Os enums passaram para `data/model/`, a pasta do seu package (16/11).
- [x] O `gradlew` ficou com permissão de execução no Git (16/11).
- [ ] Quando entrar o Firestore, as classes do modelo precisam de valores por omissão em todos os campos, porque o `toObject()` exige um construtor sem argumentos.
- A câmara usa o contrato `TakePicture`, que não exige a permissão `CAMERA` enquanto a app não a declarar no manifesto. O pedido de permissões em runtime que o enunciado exige fica para a aula #4.
- Os testes no emulador alteram os dados guardados em JSON. Os dados de exemplo só são lidos quando o ficheiro ainda não existe; para os repor: `adb shell pm clear pt.isec.amov.cityfix`.
- Dados de exemplo para testar a reabertura (J8), todos do `citizen-1`: "Iluminação" resolvida há 1 dia (mostra o botão "Reabrir") e "Saneamento" encerrada (não o mostra), ambas com histórico de estados. A "Água" também está resolvida, há 2 dias.
- [x] (Corrigido a 16/11.) Dados de exemplo trocados no commit `67596c1`: a "Iluminação" (semáforo) está Encerrada sem `closedAt` e com o histórico a terminar em Resolvido, e o "Saneamento" está Resolvido com `closedAt` e o histórico a terminar em Encerrado. Com o encerramento automático, o "Saneamento" passa a Encerrado ao arrancar, mas fica com a mudança Resolvido → Encerrado repetida no histórico, e a "Iluminação" deixa de servir para testar a reabertura. Confirmar com o João e repor os estados.
- [x] (Corrigido a 16/11: passou a usar `toDisplayText()`.) O detalhe da ocorrência (J4) escrevia as coordenadas com `stringResource(R.string.detail_location, ...)`, que formata os números na língua do dispositivo. Pelo código, em português devem sair com vírgula decimal (`40,20860, -8,42070`), ao contrário da decisão de usar sempre ponto decimal (não verificado no emulador). Pode passar a usar a `GeoLocation.toDisplayText()` da L4. Confirmar com o João.
- Os campos de texto seguem o padrão das aulas (`StateFlow`). Com escrita muito rápida num emulador lento perderam-se letras; a escrever normalmente funcionou.
- O stash `stash@{0}` (alterações antigas do João, de 11/11) já não é preciso: o `Rules.kt` deu origem ao `AccessRules.kt` e o `IncidentChart` foi recuperado.

### 3.7 Entrega
- [ ] Retirar o que é provisório (secção 2.4).
- [ ] Testar todos os ecrãs em portrait e landscape, em inglês e em português.
- [x] Relatório técnico (20/11), com o modelo do relatório de PD: `docs/Relatorio_AMOV2627.2024126292.2024113113.docx` e `.pdf`, com 35 páginas, 4 figuras do emulador e 10 tabelas. Inclui as soluções técnicas, as opções concretizadas e funcionais e as oportunidades de melhoria.
- [ ] Atualizar a secção 10.3 do relatório («Testes por fazer») e a tabela 10 depois dos testes com o João em dispositivos reais, e voltar a exportar o PDF a partir do Word.
- [ ] ZIP `AMOV2627.2024126292.2024113113`, sem `.gradle`, `build`, `app/build` nem `local.properties` (por exemplo com `git archive`, como no README).
- [ ] Entregar no Nónio até 22/11/2026, 23:59. A defesa funcional é de 23 a 27/11, com os dois presentes.

## 4. Calendário que falta (proposta)

| Quando | O quê |
|---|---|
| 12/11 | L4 com a foto de perfil (feita, com o conflito 11 fechado) |
| Depois de cada aula | Aula #4: mapa da J4, localização da J5, J6, J9, georreferenciação da L3 e F1. Firebase: L1, L2, L11 e troca dos repositórios. WorkManager: L9. Sensores: J10. Geofencing: L10 |
| 17/11 a 22/11 | Integração, limpeza, testes em landscape e em PT/EN, relatório e ZIP |
