# CityFix — Decisões e modelo de dados

Trabalho prático Android-Kotlin, AMOV 2026/27. Os números entre parênteses remetem para a lista de decisões em aberto da análise do enunciado.

## 1. Decisões tomadas

### Gerais
- Grupo de 2 alunos com Git (1).
- Backend: Firebase (2).
- O centro do raio de 10 km é a Praça da República, em Coimbra. As coordenadas definem-se mais tarde (3).
- Nomes de classes e campos em inglês (4).

### Arquitetura e organização
- Seguir os padrões dados nas aulas (ContactApp, ficha AMov.P07): MVVM com `StateFlow`, navegação type-safe, `UiState` com validações, repositório em `data/` com dados de exemplo.
- Os três perfis partilham os ecrãs sempre que possível. As regras do que cada perfil vê e faz ficam num único ficheiro, `AccessRules.kt`.
- O trabalho divide-se por ecrã, e cada ecrã tem um dono. A divisão está em `CityFix_Plano.md`.
- Nomes do modelo: mantêm-se os que já estão no código (`Incident`, `IncidentStatus`, ...). A exceção é `GeoPoint`, que passa a `GeoLocation` para não chocar com `com.google.firebase.firestore.GeoPoint` quando o Firestore entrar.
- Um repositório por entidade (`IncidentRepository`, `UserRepository`, `CategoryRepository`), cada um como o `ContactsRepository` das aulas, com o seu ficheiro JSON e dados de exemplo. São todos criados na `Application`. Um ViewModel que precise de várias entidades recebe vários repositórios na Factory.
- As regras de acesso ficam em `utils/AccessRules.kt`, junto do `Validators.kt`, como funções Kotlin puras.
- Enquanto não há login, o utilizador atual vem de uma sessão falsa: o `SessionViewModel` guarda um `StateFlow<User?>` (`null` é o anónimo) e o `SessionSelector` escolhe o perfil. Com o login (L1) sai o seletor e fica o `SessionViewModel`.
- Uma só `MainActivity` com um `NavHost` dentro de um `Scaffold`. A app abre no início (`DashboardRoute`); os ecrãs do operador (categorias, validação) e o registo abrem-se pelos botões da `MainTopAppBar`, que só aparecem ao perfil que os pode usar.
- Os três números do dashboard são calculados sobre todas as ocorrências (`IncidentsList.getDashboardStats()`); a lista mostra só as que o perfil pode ver.
- Identidade visual do CityFix (11/11/2026): paleta fixa a partir do azul-petróleo `#0B2833` do ícone, com variantes clara e escura segundo o modo do dispositivo. A barra de topo usa azul-petróleo com texto e ícones brancos. O cartão dos indicadores usa um tom mais suave da paleta e dá destaque aos valores.
- Barra de topo (11/11/2026): título em seminegrito e círculo branco translúcido atrás do ícone quando os cartões estão expandidos. A descrição do botão indica a ação disponível: expandir ou recolher.
- Detalhe da ocorrência (decidido a 11/11/2026):
  - Quem não é autor nem operador vê a categoria, a situação, o estado, a data e a descrição, e mais tarde as fotos e o mapa com zoom afastado. Ficam ocultos o autor e as coordenadas (I.1.C).
  - As coordenadas exatas e o histórico de estados aparecem ao operador e ao autor.
  - Os dados do autor (nome, e-mail, telefone) aparecem só ao operador (I.3.E).
- O munícipe filtra a lista com dois chips, "Todas" e "As minhas", por cima do dashboard.
- Criar ocorrência (decidido a 11/11/2026):
  - O botão "Nova ocorrência" é um botão flutuante (+) no início, só para o munícipe, como o + das categorias.
  - No formulário, a categoria e a situação escolhem-se em listas dropdown, para ocupar menos espaço; a visibilidade fica com `RadioButton`.
  - A base de multimédia (`FileUtils`, `FileProvider`, Coil 3 das aulas e o componente `IncidentPhoto`) foi feita pelo João com a J5; o Leonardo reaproveita-a na L4, na L7 e na L8.
  - Até haver GPS (aula #4), a localização de uma ocorrência nova é a georreferenciação do registo do autor; se não existir, é o centro do município (`CITY_CENTER`).
  - Coordenadas provisórias do centro do município, a Praça da República (3): 40.2085, -8.4196.
- Reabertura (decidido a 11/11/2026):
  - O autor reabre no detalhe, com um botão "Reabrir" e o prazo por baixo. A justificação escreve-se num `AlertDialog`.
  - Ao reabrir, o `resolvedAt` passa a `null`, porque a ocorrência deixa de estar resolvida. A data antiga fica no histórico, e a próxima resolução volta a contar os 5 dias do zero.
  - A justificação fica guardada como nota da mudança de estado (`StatusChange.note`).
  - A reabertura exige também uma foto, da galeria ou da câmara. Junta-se às fotos que a ocorrência já tem, como `MediaAttachment` com o propósito `REOPEN`.
  - O detalhe mostra todas as fotos numa fila com scroll horizontal; a primeira continua a ser o thumbnail da lista.

### Acesso e perfis
- Anónimo fora do raio de 10 km: vê apenas o ecrã de login (5).
- Munícipe pendente: não inicia sessão. Aparece um alerta a avisar que não é possível iniciar sessão (6).
- Domínio de e-mail dos operadores: `@aac-coimbra.pt` (9).
- Validações do registo:
  - e-mail com a expressão regular das aulas; o operador tem de usar `@aac-coimbra.pt`;
  - telefone com 9 dígitos, a começar por 2 ou 9 (formato português);
  - NIF com 9 dígitos, sem verificar o dígito de controlo; único por perfil, como diz o enunciado (o mesmo NIF pode existir num munícipe e num operador);
  - o e-mail não é verificado como único; o Firebase Authentication fá-lo quando entrar (ponto 7).
- Campos editáveis no perfil: foto, morada, nome, telefone (11). Há um conflito com o enunciado, ver secção 2.

### Ocorrências
- A foto é obrigatória na criação. A localização da ocorrência é obtida no momento em que a foto é tirada (12).
- Anexos por ocorrência: 1 foto e 1 vídeo na criação, mais 1 foto obrigatória em cada reabertura (13; alterado a 11/11/2026).
- Ocorrência ativa: qualquer estado exceto Encerrado (14).
- Tempo médio de reparação: de Aberto a Encerrado (15).
- O total do dashboard inclui as ocorrências privadas. Os detalhes e as fotos das privadas não são mostrados (16).
- A lista mostra ao anónimo e ao munícipe só as ocorrências públicas e não encerradas, como diz o enunciado (I.1.B); o munícipe vê também as suas, e o operador vê todas. As privadas contam só para os números do dashboard (16). Regra em `utils/AccessRules.kt` (`canViewIncident`).

### Ocorrência de grupo
- O critério de agrupamento é só a categoria, num raio de 10 m (18).
- A primeira ocorrência criada é a principal. As novas ligam-se a ela (19).
- O estado e as mensagens ficam em cada ocorrência, não no grupo (20).
- Uma ocorrência privada fica anexada ao grupo, mas os seus detalhes e fotos não são mostrados (21).
- Ocorrências encerradas não contam para o agrupamento (22).

### Ciclo de vida
- Depois de um Pedido de Informações, o ticket volta ao estado de onde saiu (23).
- Só um pedido feito pelo operador muda o estado para Pedido de Informações. Um pedido do munícipe não muda o estado (24).
- As mensagens existem só associadas a pedidos de informação (25).
- Um pedido fica respondido na primeira resposta da outra parte. Num pedido do operador, essa resposta repõe o estado anterior da ocorrência (C, 11/11/2026).
- As respostas aos pedidos de informação são apenas em texto (D, 11/11/2026).
- O munícipe pode abrir um pedido de informação numa ocorrência da sua autoria em qualquer estado exceto Encerrado. O pedido mantém o estado da ocorrência (E, 11/11/2026).
- O operador só pode abrir um pedido de informação em Aberto ou Em Resolução. Enquanto o pedido está pendente, a ocorrência está em Pedido de Informações (11/11/2026).
- O autor e o operador criam pedidos e respondem na secção «Pedidos de informação» do detalhe. O botão «Pedir informação» está nessa secção para os dois perfis (11/11/2026).
- Reabertura sem limite. O ticket volta para Aberto (26).
- O registo do início dos trabalhos corresponde à passagem para Em Resolução (27).

## 2. Conflitos com o enunciado (a confirmar)

- **Edição de perfil (11):** o enunciado proíbe alterar o nome, tanto no munícipe como no operador. A decisão tomada inclui o nome nos campos editáveis.
- **Morada (11):** o enunciado não tem um campo "morada", só "georreferenciação", que o munícipe não pode alterar. Falta decidir se a morada é um campo novo, separado da georreferenciação, ou se é a própria georreferenciação.
- **Tempo médio de reparação (15):** um ticket só passa a Encerrado 5 dias depois de Resolvido. Medir de Aberto a Encerrado acrescenta sempre esses 5 dias à média. Confirmar se é intencional.

## 3. Por confirmar

### Da análise inicial
- (7) O mesmo NIF ou e-mail pode existir como munícipe e como operador?
- (8) A georreferenciação do registo vem do GPS ou é escolhida no mapa? É uma morada ou só coordenadas?
- (10) Que segundo fator se usa no MFA?
- (13) O limite de 1 foto e 1 vídeo é definitivo?
- (17) O que mostra o mapa do detalhe público: um ponto aproximado, uma zona ou só o município?
- (28) Quem executa o encerramento automático aos 5 dias e o aviso das 24 h: o dispositivo ou o backend?
- (29) A geofence funciona para ocorrências ativas (secção I do enunciado) ou só em Em Resolução (secção II)?
- (30) Qualquer operador ativa a geofence, ou só um operador atribuído? Há atribuição de operador a ocorrências?
- (31) Na Deteção de Impactos, que categoria corresponde a "pavimento" e que limiar de aceleração se usa?
- (32) As notificações ficam guardadas na app ou são só avisos do sistema?

### Novas, resultantes das decisões
- **A.** Um vídeo pode substituir a foto, ou a foto é sempre obrigatória e o vídeo opcional?
- **B.** Se a ocorrência principal for encerrada e houver ligadas ainda ativas, uma nova ocorrência no mesmo local liga-se a quê? À ligada ativa mais antiga, ou passa a ser uma nova principal?

## 4. Modelo de dados

Implementado em `data/model` (tarefa 0.1). As classes guardadas têm `@Serializable` para o repositório em JSON.

### Enums
- `UserRole`: CITIZEN, OPERATOR. O anónimo não é guardado; corresponde a não haver sessão.
- `AccountStatus`: PENDING, ACTIVE.
- `Situation`: DAMAGE, MALFUNCTION, DESTRUCTION, THEFT.
- `Visibility`: PUBLIC, PRIVATE.
- `IncidentStatus`: OPEN, INFO_REQUESTED, IN_PROGRESS, RESOLVED, CLOSED.
- `MediaType`: IMAGE, VIDEO.
- `MediaPurpose`: REPORT, REOPEN, RESOLUTION_PROOF.
- `UploadStatus`: PENDING, UPLOADING, UPLOADED, FAILED.
- `InfoRequestStatus`: OPEN, ANSWERED.

### `GeoLocation`
- `latitude`
- `longitude`
- No ecrã, as coordenadas aparecem com 5 casas decimais e ponto decimal em qualquer língua (`Locale.ROOT`), para a vírgula decimal portuguesa não se confundir com a vírgula que separa a latitude da longitude.

### `User`
- `id`: UUID por agora; passa a ser o uid do Firebase Authentication quando este entrar.
- `name`, `email`, `phone`, `nif`
- `role`
- `status` (`AccountStatus`): o munícipe começa em PENDING e o operador em ACTIVE.
- `createdAt`
- `picturePath` (opcional): caminho da foto, como no ContactsApp.
- `location` (opcional): georreferenciação do registo; só o munícipe a tem.
- `impactDetectionEnabled` (`false` por omissão): só faz sentido no munícipe.
- `address` (opcional, `null` por omissão): fica assim até se fechar o conflito da morada (secção 2).

### `Category`
- `id`, `name`
- `active`
- `createdBy` (id do operador)
- `createdAt`, `updatedAt`

### `Incident`
- `id`, `authorId`
- `categoryId`, `categoryName` (cópia, para manter o nome se a categoria for editada)
- `situation`, `description`, `visibility`
- `location` (obtida quando se tira a foto)
- `status`
- `previousStatus` (estado para onde volta depois de um pedido de informação do operador)
- `media` (lista de `MediaAttachment`)
- `history` (lista de `StatusChange`): o histórico fica dentro da própria ocorrência.
- `infoRequests` (lista de `InfoRequest`) e `messages` (lista de `Message`): gravados no mesmo registo da ocorrência no repositório local, juntamente com as mudanças de estado.
- `primaryIncidentId` (vazio se for a principal ou se não pertencer a um grupo)
- `createdAt`, `workStartedAt` (entrada em Em Resolução), `resolvedAt`, `closedAt`
- `reopenCount`

### `MediaAttachment`
- `id`, `type`, `purpose`
- `localUri` (enquanto o envio está pendente)
- `remoteUrl`, `thumbnailUrl`
- `uploadStatus`
- `uploadedBy`, `createdAt`

### `StatusChange` (histórico do ticket)
- `fromStatus`, `toStatus`
- `timestamp`
- `changedBy`
- `note` (por exemplo, a justificação da reabertura; por isso a ocorrência não tem um campo `justification`)

### `InfoRequest`
- `id`, `incidentId`
- `requestedBy`, `requesterRole`
- `text`
- `status` (`InfoRequestStatus`)
- `createdAt`, `answeredAt`
- Só muda o estado do ticket se `requesterRole` for OPERATOR.

### `Message` (resposta a um pedido de informação)
- `id`, `infoRequestId`
- `senderId`, `senderRole`
- `text`
- `timestamp`

### `DashboardStats` (calculado, não guardado)
- `averageRepairTimeMillis`: média de `closedAt` − `createdAt`; `null` se ainda não houver ocorrências encerradas.
- `totalIncidents`: inclui as privadas.
- `activePercentage`: ocorrências com estado diferente de CLOSED.

### Coleções e repositórios
- `IncidentsList`: coleção de ocorrências, como o `ContactsList` (`findById`, `upsert`, `rawList`, `getAllSorted` da mais recente para a mais antiga).
- `IncidentRepository`: grava em `incidents_database.json` e tem dados de exemplo. É criado na `CityFixApp` e passado ao `IncidentsViewModel` pela `IncidentsViewModelFactory`.
- `CategoriesList`: coleção de categorias, ordenada pelo nome com `Collator` (acentos). `getActiveSorted()` devolve só as ativas, para a criação de ocorrências.
- `CategoryRepository`: grava em `categories_database.json`, com dados de exemplo cujos ids coincidem com os das ocorrências de exemplo.
- `UsersList`: coleção de utilizadores, com `hasNif(nif, role)` para a unicidade do NIF por perfil e `getPendingCitizensSorted()` para a validação de munícipes.
- `UserRepository`: grava em `users_database.json`, com dados de exemplo cujos ids coincidem com os autores das ocorrências (`citizen-1`, `citizen-2`) e com o criador das categorias (`operator-1`).

### Removido em relação à primeira proposta
- `OccurrenceGroup`: o grupo passa a ser representado por `primaryIncidentId` na própria ocorrência (decisões 19 e 20).

### Regras de negócio que o modelo tem de suportar
- Só categorias ativas aparecem na criação de ocorrências.
- Ao criar, procura-se uma ocorrência principal não encerrada, da mesma categoria, a menos de 10 m. Se existir, a nova liga-se a ela.
- Um pedido de informação do operador guarda o estado atual em `previousStatus` e passa o ticket a INFO_REQUESTED. Na primeira resposta do autor, o ticket volta a `previousStatus` e esse campo é limpo.
- A reabertura só é possível em RESOLVED, até 5 dias depois de `resolvedAt`. Passa o ticket a OPEN, incrementa `reopenCount` e exige justificação.
- Um ticket em CLOSED não aceita alterações.
- Cada transição de estado gera um `StatusChange`.
