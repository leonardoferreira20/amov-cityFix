# Tarefas — Leonardo Ferreira

Plano geral em [CityFix_Plano.md](CityFix_Plano.md). Molde das aulas: ContactApp e ficha AMov.P07.

## Fase 0 — com o João

- [x] 0.1 a 0.7, descritas em [CityFix_Plano.md](CityFix_Plano.md), secção 4. Concluídas a 11/11/2026.

## Tarefas

### L3 — Registo (munícipe e operador)
- [x] Um só ecrã para os dois perfis, com `RegisterUiState` no estilo do `EditContactUiState`: campos, erro por campo e `isValid`.
- [x] Campos comuns: nome, e-mail, telefone, NIF, perfil.
- [x] Validações (em `utils/Validators.kt`, para a L4 as reaproveitar):
  - e-mail e telefone com expressão regular, como no ContactApp;
  - NIF com 9 dígitos e único para o mesmo perfil (verificado no repositório);
  - operador: e-mail do domínio `@aac-coimbra.pt` (decisão 9).
- [x] Munícipe: opção "Deteção de Impactos"; conta fica `PENDING`.
- [x] Operador: conta fica `ACTIVE`.
- [x] Gravar o utilizador através do repositório.
- **Feito quando:** os dois perfis registam-se com dados válidos e os erros aparecem campo a campo.
- [x] Criação da conta no Firebase Authentication, com password e confirmação (16/11).
- [x] Georreferenciação do munícipe (aula #4, 16/11): `MapLocationPicker` (toque no mapa, como o da P10) ou «Usar a minha localização atual»; obrigatória para o munícipe; fica em `User.location`.
- [x] Foto opcional no registo (L8): avatar circular (`UserAvatar`) com as iniciais do nome e os botões Galeria / Câmara no topo do formulário; fica em `User.picturePath`.
- [x] Envio do e-mail de verificação ao registar (necessário para o MFA).

### L4 — Perfil
- [x] Conflito 11 de [CityFix_Decisoes.md](CityFix_Decisoes.md) fechado a 12/11/2026, a favor do enunciado: o nome não se altera e não há morada (o `address` saiu do `User`).
- [x] Ecrã de edição no estilo do `EditScreen` (`ProfileScreen`, `ProfileUiState`, `ProfileViewModel`), reaproveitando a validação do telefone do L3.
- [x] Campos bloqueados conforme o enunciado: nome e NIF para os dois perfis; georreferenciação também para o munícipe. O e-mail também fica só de leitura.
- [x] Separar os dados só de leitura da edição do telefone, foto e deteção de impactos. Permitir remover a foto no formulário e gravar a alteração.
- [x] Deteção de Impactos editável no perfil do munícipe.
- [x] Foto de perfil (L8), com o mesmo avatar e os mesmos botões do registo (`UserPhotoField`).
- [x] Botão de conta na barra de topo, só para o munícipe e o operador (`canEditProfile`).
- [x] Botão «Terminar sessão» no topo do início para ambos os perfis autenticados e também no perfil.
- **Feito quando:** os campos bloqueados não se editam e as alterações ficam gravadas. Verificado no emulador a 12/11/2026.

### L5 — Validação de munícipes
- [x] Ecrã só para o operador: o botão de acesso só aparece na barra de topo com o perfil Operador (`canValidateCitizens`).
- [x] Lista dos munícipes `PENDING` com nome, georreferenciação e NIF.
- [x] Botão para ativar: passa a `ACTIVE`.
- **Feito quando:** um munícipe ativado sai da lista de pendentes.

### L6 — Categorias
- [x] Lista de categorias.
- [x] Só para o operador: o botão de acesso só aparece na barra de topo com o perfil Operador (`canManageCategories`). O criador da categoria é o utilizador da sessão.
- [x] Um só ecrã para criar e editar, com `EditCategoryRoute(categoryId: String? = null)`, como o `EditContactRoute`.
- [x] Ativar e desativar.
- [x] Só as categorias ativas aparecem na criação de ocorrências: a J5 usa `CategoriesList.getActiveSorted()` (verificado no emulador: "Espaços Verdes", inativa, não aparece).
- [x] Dois toques seguidos no ✓ gravam a categoria uma só vez (`isSaving`, 12/11/2026).
- **Feito quando:** uma categoria desativada deixa de aparecer na criação de ocorrências.

### L7 — `LifecycleActions`
- [x] Componente stateless `LifecycleActions` com o contrato da tarefa 0.6, no espaço `operatorActions` do detalhe, dentro da secção «Gestão do estado».
- [x] Só mostra as transições válidas a partir do estado atual (`canStartWork` e `canResolve` em `AccessRules.kt`).
- [x] Cada transição regista um `StatusChange` com timestamp, e o histórico aparece no detalhe.
- [x] Pedido de informação do operador disponível na secção «Pedidos de informação» do detalhe (J7 do João).
- [x] Início dos trabalhos: passa a `IN_PROGRESS` e preenche `workStartedAt` (decisão 27).
- [x] Resolver: só a partir de `IN_PROGRESS`; passa a `RESOLVED`, preenche `resolvedAt`, exige uma foto comprovativa (`RESOLUTION_PROOF`) e aceita uma nota opcional.
- [x] Encerrado não aceita alterações. Cinco dias depois de resolvida, a ocorrência passa a Encerrada ao carregar as ocorrências (ponto 28).
- **Feito quando:** o operador leva uma ocorrência de Aberto a Resolvido e o histórico mostra cada passo. Verificado no emulador a 11/11/2026.
- 📝 **Anotado:** vídeo comprovativo (pontos 13 e A) e notificação ao autor quando a ocorrência é resolvida (L11).

### L1 — Login (16/11, documentação oficial do Firebase)
- [x] `LoginScreen` + `LoginViewModel` com e-mail e password (`signInWithEmailAndPassword`).
- [x] Conta pendente: termina a sessão e mostra o aviso (decisão 6).
- [x] Sessão guardada pelo Firebase e retomada ao abrir (`SessionViewModel`); «Terminar sessão» no perfil.
- [x] Saiu o seletor de perfil.

### L2 — MFA e recuperação (16/11)
- [x] Recuperação por e-mail (`sendPasswordResetEmail`) no login.
- [x] MFA com TOTP, opcional, ativado no perfil depois de confirmar o e-mail (`TotpMultiFactorGenerator`); pedido no login quando está ativo.
- [x] Login, conta pendente e recuperação testados com o Firebase a 16/11.
- [ ] Testar a verificação em dois passos com uma conta real e o TOTP ativo no projeto (Cloud Shell).

### L9 — Fotos no Storage (17/11, documentação oficial)
- [x] Compressão (1280 px, JPEG a 80%) e thumbnail (320 px, JPEG a 70%) antes do envio, com a rotação EXIF (`ImageCompressor`).
- [x] Envio com WorkManager só com rede: um trabalho por foto, até 5 novas tentativas (`MediaUploadScheduler`, `MediaUploadWorker`).
- [x] No fim do envio, a ocorrência guarda os endereços da foto e do thumbnail, numa transação (`IncidentRepository.markMediaUploaded`).
- [x] As listas mostram o thumbnail e o detalhe a foto do Storage; enquanto não chega, aparece a foto local e o aviso «Há fotos que ainda não foram enviadas».
- [x] Regras do Storage em `storage.rules`.
- [ ] Ativar o Storage no projeto (plano Blaze) e publicar o `storage.rules` (João, dono do projeto).
- [ ] Testar entre dois dispositivos: o munícipe cria a ocorrência com foto e o operador vê-a.
- **Feito quando:** a foto tirada num telemóvel aparece noutro.

### F1 — Landscape e acessibilidade nos meus ecrãs (17/11)
- [x] Registo e perfil com duas colunas em landscape, no padrão do João (P10); uma coluna em portrait.
- [x] Categorias e validação numa grelha adaptável: uma coluna em portrait, duas em landscape.
- [x] O login e a edição de categoria ficam numa coluna (o login já tem largura máxima).
- [x] Títulos de secção como cabeçalhos, dados da conta lidos como um só elemento e erros anunciados ao aparecer.
- [ ] Testar em landscape o perfil, as categorias e a validação com sessão iniciada.

### L11 — Notificações (17/11, documentação oficial)
- [x] Cloud Functions com o Cloud Messaging (decidido a 17/11); só avisos do sistema, e tocar abre o detalhe (ponto 32).
- [x] Token do Cloud Messaging em `deviceTokens/<uid>` ao iniciar sessão, tópico `operators` para os operadores e token apagado ao terminar sessão (`DeviceTokenRepository`).
- [x] `CityFixMessagingService` e `IncidentNotifications`: canal próprio e textos do `strings.xml` em inglês e português.
- [x] `notifyIncidentChanges`: mudança de estado, pedidos de informação, respostas e proximidade (L10). Quem fez a alteração não é avisado.
- [x] `closeResolvedIncidents`, de hora a hora: aviso das 24 h e encerramento aos 5 dias no servidor (ponto 28).
- [ ] Publicar as funções e as regras: plano Blaze, Node.js 22, Firebase CLI e acesso ao projeto do João.
- [ ] Testar em dois dispositivos: o operador muda o estado e o autor recebe a notificação; o autor responde e o operador que perguntou recebe-a.
- **Feito quando:** cada um dos quatro tipos de notificação do enunciado chega ao telemóvel certo com a app fechada.

## Anotado — tarefas a criar depois da aula

| Tarefa | O que é | Aula |
|---|---|---|
| L8 | Multimédia: a base já existe (feita pelo João com a J5: `utils/FileUtils`, `FileProvider`, Coil 3 e os componentes `IncidentPhoto` e `PhotoPicker`; basta pôr o `PhotoPicker` no ecrã e guardar o caminho que ele devolve). Já é usada na foto opcional do registo (L3, com o `UserAvatar`), na foto de perfil (L4) e na prova de resolução (L7); o vídeo está por definir (ponto 13 por confirmar) | #3 |
| L10 | A notificação ao autor foi feita com a L11 (17/11); falta publicar as funções. Já feito a 16/11: `ProximityService` em primeiro plano para o operador, alerta gravado na ocorrência (`ProximityAlert`) para Aberto e Pedido de informações a menos de 20 m, secção no detalhe e pedido da localização em 2.º plano | Firebase |

## Sempre, em todos os meus ecrãs

- [ ] Textos em `strings.xml`, em inglês e em `values-pt`.
- [ ] Funciona em portrait e landscape.
- [ ] Descrições de acessibilidade nos ícones e botões.
- [ ] Preview com dados de exemplo.

## Pontos por confirmar que são meus

13 e 30 — ver [CityFix_Decisoes.md](CityFix_Decisoes.md), secção 3. O 7, o 8, o 10 e o 29 ficaram fechados a 16/11, o 28 e o 32 a 17/11 (L11) e o conflito 11 a 12/11/2026.
