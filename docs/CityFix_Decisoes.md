# CityFix — Decisões e modelo de dados

Trabalho prático Android-Kotlin, AMOV 2026/27. Os números entre parênteses remetem para a lista de decisões em aberto da análise do enunciado.

## 1. Decisões tomadas

### Gerais
- Grupo de 2 alunos com Git (1).
- Backend: Firebase (2).
- O centro do raio de 10 km é a Praça da República, em Coimbra. As coordenadas definem-se mais tarde (3).
- Nomes de classes e campos em inglês (4).

### Acesso e perfis
- Anónimo fora do raio de 10 km: vê apenas o ecrã de login (5).
- Munícipe pendente: não inicia sessão. Aparece um alerta a avisar que não é possível iniciar sessão (6).
- Domínio de e-mail dos operadores: `@aac-coimbra.pt` (9).
- Campos editáveis no perfil: foto, morada, nome, telefone (11). Há um conflito com o enunciado, ver secção 2.

### Ocorrências
- A foto é obrigatória na criação. A localização da ocorrência é obtida no momento em que a foto é tirada (12).
- Anexos por ocorrência: 1 foto e 1 vídeo, por agora (13).
- Ocorrência ativa: qualquer estado exceto Encerrado (14).
- Tempo médio de reparação: de Aberto a Encerrado (15).
- O total do dashboard inclui as ocorrências privadas. Os detalhes e as fotos das privadas não são mostrados (16).

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
- Reabertura sem limite. O ticket volta para Aberto (26).
- O registo do início dos trabalhos corresponde à passagem para Em Resolução (27).

## 2. Conflitos com o enunciado (a confirmar)

- **Edição de perfil (11):** o enunciado proíbe alterar o nome, tanto no munícipe como no operador. A decisão tomada inclui o nome nos campos editáveis.
- **Morada (11):** o enunciado não tem um campo "morada", só "georreferenciação", que o munícipe não pode alterar. Falta decidir se a morada é um campo novo, separado da georreferenciação, ou se é a própria georreferenciação.
- **Privadas na listagem pública (16):** o enunciado diz que o anónimo vê a "listagem de ocorrências públicas". Falta decidir se as privadas aparecem nessa lista, sem detalhes nem fotos, ou se só contam para os números do dashboard.
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
- **C.** Quando fica respondido um pedido de informação: na primeira resposta, ou quando quem o pediu o fecha?
- **D.** As respostas a pedidos de informação podem ter anexos (foto ou vídeo)?
- **E.** Um munícipe pode abrir um pedido de informação em qualquer estado, ou só em alguns?

## 4. Modelo de dados (proposta atualizada)

### Enums
- `UserRole`: CITIZEN, OPERATOR. O anónimo não é guardado; corresponde a não haver sessão.
- `AccountStatus`: PENDING, ACTIVE.
- `Situation`: DAMAGE, MALFUNCTION, DESTRUCTION, THEFT.
- `Visibility`: PUBLIC, PRIVATE.
- `TicketStatus`: OPEN, INFO_REQUESTED, IN_PROGRESS, RESOLVED, CLOSED.
- `MediaType`: IMAGE, VIDEO.
- `MediaPurpose`: REPORT, RESOLUTION_PROOF. Juntar INFO_REPLY se D for sim.
- `UploadStatus`: PENDING, UPLOADING, UPLOADED, FAILED.
- `InfoRequestStatus`: OPEN, ANSWERED.

### `GeoLocation`
- `latitude`
- `longitude`

### `User`
- `id` (uid da autenticação)
- `name`, `email`, `phone`, `nif`
- `role`
- `photoUrl` (opcional)
- `createdAt`
- Só para munícipes:
  - `location` (georreferenciação do registo)
  - `status` (`AccountStatus`)
  - `impactDetectionEnabled`
- `address`: só se a morada for um campo separado (secção 2).

### `Category`
- `id`, `name`
- `active`
- `createdBy` (id do operador)
- `createdAt`, `updatedAt`

### `Occurrence`
- `id`, `authorId`
- `categoryId`, `categoryName` (cópia, para manter o nome se a categoria for editada)
- `situation`, `description`, `visibility`
- `location` (obtida quando se tira a foto)
- `status`
- `previousStatus` (estado para onde volta depois de um pedido de informação do operador)
- `media` (lista de `MediaAttachment`)
- `primaryOccurrenceId` (vazio se for a principal ou se não pertencer a um grupo)
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
- `note` (por exemplo, a justificação da reabertura)

### `InfoRequest`
- `id`, `occurrenceId`
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
- `averageRepairTime`: média de `closedAt` − `createdAt`.
- `totalOccurrences`: inclui as privadas.
- `activePercentage`: ocorrências com estado diferente de CLOSED.

### Removido em relação à primeira proposta
- `OccurrenceGroup`: o grupo passa a ser representado por `primaryOccurrenceId` na própria ocorrência (decisões 19 e 20).

### Regras de negócio que o modelo tem de suportar
- Só categorias ativas aparecem na criação de ocorrências.
- Ao criar, procura-se uma ocorrência principal não encerrada, da mesma categoria, a menos de 10 m. Se existir, a nova liga-se a ela.
- Um pedido de informação do operador guarda o estado atual em `previousStatus` e passa o ticket a INFO_REQUESTED. Quando é respondido, o ticket volta a `previousStatus`.
- A reabertura só é possível em RESOLVED, até 5 dias depois de `resolvedAt`. Passa o ticket a OPEN, incrementa `reopenCount` e exige justificação.
- Um ticket em CLOSED não aceita alterações.
- Cada transição de estado gera um `StatusChange`.
