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
- [x] Vídeo comprovativo opcional (20/11, pontos 13 e A) e notificação ao autor quando a ocorrência é resolvida (L11).

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
- [x] Regras de produção do Storage em `storage.rules`. O projeto usa o modo de teste, por indicação do professor (17/11), por isso estas regras não são publicadas.
- [x] Projeto no plano Blaze (confirmado a 17/11).
- [x] Storage ativado em modo de teste (17/11), no bucket `cityfix-3dbb1.firebasestorage.app`.
- [x] Envio testado no emulador a 17/11: worker com sucesso, foto e thumbnail (320 px) no Storage, Firestore com `UPLOADED` e os endereços, thumbnail mostrado a partir do Storage.
- [x] O operador vê a foto da ocorrência criada pelo munícipe (confirmado pelo Leonardo a 17/11, no emulador). A foto vem do endereço do Storage, por isso não depende do telemóvel onde foi tirada.
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
- [x] Funções e regras do Firestore publicadas a 17/11 com o Firebase CLI: `notifyIncidentChanges` em `europe-southwest1`, a região do Firestore, e `closeResolvedIncidents` em `europe-west1`, porque o Cloud Scheduler não existe em Madrid.
- [x] A `closeResolvedIncidents` de `europe-southwest1`, que sobrou da primeira tentativa, foi apagada na consola (confirmado a 20/11). Ficam a `notifyIncidentChanges` em `europe-southwest1` e a `closeResolvedIncidents` em `europe-west1`.
- [x] Proximidade testada a 17/11 com dois emuladores: o operador ficou a menos de 20 m da «Iluminação» e o munícipe recebeu a notificação com a app em segundo plano.
- [ ] Testar em dois dispositivos reais, com o João: o operador muda o estado e o autor recebe a notificação; o operador pede informação e o autor recebe-a; o autor responde e o operador que perguntou recebe-a; o autor faz um pedido e os operadores recebem-no; tocar na notificação abre o detalhe. A 17/11 a função correu na resposta e no pedido do operador, mas o segundo emulador foi abaixo por falta de memória e não os recebeu (o Mac não aguenta dois emuladores com o Android 17).
- [x] A `closeResolvedIncidents` corre de hora a hora, sem erros nos registos (confirmado a 20/11).
- **Feito quando:** cada um dos quatro tipos de notificação do enunciado chega ao telemóvel certo com a app fechada.

## Recebidas do João (20/11)

A 20/11 o Leonardo ficou com o que faltava do João. O que o João já fez está em [Tarefas_Joao.md](Tarefas_Joao.md).

### J10 — Deteção de impactos
- [x] Ponto 31 e desenho decididos a 20/11 (ver [CityFix_Decisoes.md](CityFix_Decisoes.md)): serviço em primeiro plano, 6 m/s² na vertical a mais de 10 km/h, no máximo uma sugestão por minuto, «Vias Públicas» e «Dano» pré-escolhidas e o ponto do impacto marcado no mapa.
- [x] `ImpactDetector` (filtro da documentação oficial e projeção na direção da gravidade), com 6 testes unitários (`ImpactDetectorTest`).
- [x] `ImpactDetectionService`: acelerómetro e localização num serviço em primeiro plano, arrancado pela `MainActivity` para o munícipe com a deteção ativa e parado quando a desliga ou termina sessão.
- [x] A sugestão é uma notificação (`IncidentNotifications.showImpactSuggestion`) que abre a `ReportIncidentRoute(impactLatitude, impactLongitude)` com os campos pré-preenchidos.
- [x] Testado no emulador a 20/11 com o «Municipe Teste», o GPS a 18,5 km/h e um solavanco de 20 m/s² no acelerómetro virtual: a sugestão chegou com a app em segundo plano e abriu a «Nova ocorrência» com «Vias Públicas», «Dano» e o ponto do impacto. Parado, o mesmo solavanco não dá sugestão. Rodar o ecrã mantém o formulário sem o abrir duas vezes, e desligar a deteção no perfil para o serviço.
- **Feito quando:** um munícipe com a deteção ativa, em deslocação, recebe a sugestão de abrir uma ocorrência de pavimento depois de uma aceleração vertical brusca.

### Vídeo na nova ocorrência (J5) e na prova de resolução (L7)
- [x] Pontos 13 e A decididos a 20/11: foto obrigatória e no máximo 1 vídeo opcional, na nova ocorrência e na prova de resolução; `VideoView` sem bibliotecas novas; 30 s a gravar e 50 MB da galeria.
- [x] `VideoPicker` (gravar com `CaptureVideo` ou escolher com `PickVisualMedia` só com vídeos), `IncidentVideo` (`VideoView` num `AndroidView`) e envio pelo `MediaUploadWorker`, sem compressão.
- [x] Testado no emulador a 20/11 com um vídeo de teste da galeria: pré-visualização, envio com sucesso e reprodução no detalhe a partir do Storage.
- [ ] Testar a gravação com a câmara e o vídeo na prova de resolução, de preferência num telemóvel real (a câmara traseira do emulador é a webcam do Mac).

### Revisão dos ecrãs do João
- [x] Textos em `strings.xml` (inglês e português), portrait e landscape, descrições de acessibilidade e previews no início, na lista, no detalhe, na nova ocorrência, no mapa, nos pedidos de informação e na reabertura.
- [x] Revisto no código a 20/11: todos os textos em inglês e português e nenhum fixo no código; os 9 ícones sem descrição são decorativos; títulos de secção do início, das estatísticas, da lista e do detalhe marcados como cabeçalhos (`heading()`).
- [x] Revisão final dos ecrãs do João (20/11): no emulador, em portrait e landscape e em inglês e português, o início, a lista, o detalhe, a nova ocorrência e o mapa estão corretos. Corrigido o detalhe em landscape: a coluna da direita (fotos, vídeo e mapa) passou a deslocar-se e o mapa tem 200 dp, como em portrait; antes, com o vídeo, o mapa deixava de aparecer. Títulos de secção marcados como cabeçalhos e previews novos no cartão, nos pedidos de informação e no mapa.

## Anotado — tarefas a criar depois da aula

| Tarefa | O que é | Aula |
|---|---|---|
| L8 | Multimédia: a base já existe (feita pelo João com a J5: `utils/FileUtils`, `FileProvider`, Coil 3 e os componentes `IncidentPhoto` e `PhotoPicker`; basta pôr o `PhotoPicker` no ecrã e guardar o caminho que ele devolve). Já é usada na foto opcional do registo (L3, com o `UserAvatar`), na foto de perfil (L4) e na prova de resolução (L7); o vídeo opcional foi feito a 20/11 (`VideoPicker` e `IncidentVideo`) | #3 |
| L10 | Feita. A notificação ao autor foi feita com a L11, publicada e testada entre dois emuladores a 17/11. Já feito a 16/11: `ProximityService` em primeiro plano para o operador, alerta gravado na ocorrência (`ProximityAlert`), secção no detalhe e pedido da localização em 2.º plano. Desde 20/11 o alerta vale para todas as ativas, incluindo Em resolução (II.3), e qualquer operador o gera (ponto 30) | Firebase |

## Sempre, em todos os meus ecrãs

- [x] Textos em `strings.xml`, em inglês e em `values-pt`.
- [x] Funciona em portrait e landscape.
- [x] Descrições de acessibilidade nos ícones e botões.
- [x] Preview com dados de exemplo.

## Pontos por confirmar que são meus

Nenhum — ver [CityFix_Decisoes.md](CityFix_Decisoes.md), secção 3. O 13 e o A (vídeo) ficaram fechados a 20/11. O 7, o 8, o 10 e o 29 ficaram fechados a 16/11, o 28 e o 32 a 17/11 (L11), o conflito 11 a 12/11/2026 e, a 20/11, o 31 (J10), o 17, o 30, o B, o conflito 15 e a revisão do 29.
