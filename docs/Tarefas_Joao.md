# Tarefas — João Carvalho

Plano geral em [CityFix_Plano.md](CityFix_Plano.md). Molde das aulas: ContactApp e ficha AMov.P07.

## Passadas para o Leonardo (20/11/2026)

A 20/11 o Leonardo ficou com o que faltava do João, para acabar o trabalho até à entrega. Está em [Tarefas_Leonardo.md](Tarefas_Leonardo.md), secção «Recebidas do João»:
- J10 Deteção de impactos;
- vídeo na nova ocorrência (J5, ponto A);
- revisão final dos ecrãs do João (textos em inglês e português, portrait e landscape, acessibilidade e previews);
- pontos por confirmar 17, 31, A e B.

As notificações dos pedidos e das respostas (J7) já foram feitas com a L11.

## Fase 0 — com o Leonardo

- [x] 0.1 e 0.2 (feitas pelo Leonardo).
- [x] 0.3 `utils/AccessRules.kt`.
- [x] 0.4 Sessão falsa: `SessionViewModel` e `SessionSelector`.
- [x] 0.5 `NavHost` único com `Scaffold` e `MainTopAppBar`; saíram o `Screen.JOAO` / `Screen.LEONARDO` e o `DevMenuScreen`.
- [x] 0.6 Contratos do `LifecycleActions` (fechado com a L7 do Leonardo, a 11/11/2026) e do componente de mensagens, na secção 7 do plano.
- [x] 0.7 Textos em `strings.xml` (EN e PT); os textos e as cores de teste do `MainScreen` saíram.

Descrição de cada uma em [CityFix_Plano.md](CityFix_Plano.md), secção 4.

## Pedidos do Leonardo (17/11/2026)

O Leonardo enviou a L9 (fotos no Storage), a L11 (notificações com Cloud Functions) e o F1 (landscape nos ecrãs dele) no commit `5fa83d7`. Para funcionarem, faltam estes passos na consola do Firebase, que são do João, por ser o dono do projeto:

- [x] Passar o projeto `cityfix-3dbb1` para o plano Blaze (confirmado a 17/11). Pode criar-se um alerta de orçamento na Google Cloud.
- [x] Ativar o Storage em modo de teste, por indicação do professor: feito pelo Leonardo a 17/11 (bucket `cityfix-3dbb1.firebasestorage.app`).
- [x] Dar ao Leonardo acesso ao projeto: já tinha, e publicou as funções e as regras do Firestore com o Firebase CLI a 17/11.
- [x] Confirmar a localização da base de dados do Firestore: está em `europe-southwest1` (Madrid). A `notifyIncidentChanges` ficou nessa região e a `closeResolvedIncidents` em `europe-west1`, porque o Cloud Scheduler não existe em Madrid (17/11).
- [ ] As funções já estão publicadas (17/11) e a notificação de proximidade já foi testada entre dois emuladores. Falta testarem os dois em dispositivos separados: a foto de uma ocorrência nova vista noutro telemóvel e as notificações de mudança de estado, pedidos, respostas e proximidade.

Mudanças no código partilhado que convém conhecer:
- O `IncidentRepository` recebe um `MediaUploadScheduler`: o `saveIncident` agenda o envio das fotos que ainda não foram enviadas, e o `markMediaUploaded` guarda os endereços no fim.
- A `Incident` tem o campo `closingWarningSentAt`, escrito pelo servidor quando envia o aviso das 24 h.
- Em `IncidentPhoto.kt`, o `firstPhotoPath()` passou a `firstThumbnailPath()`, e o `photoPaths()` dá preferência ao endereço do Storage.
- O detalhe mostra «Há fotos que ainda não foram enviadas» enquanto o envio não acaba.
- O `SessionViewModel` e a sua `Factory` recebem o `DeviceTokenRepository`, que guarda o token do Cloud Messaging ao iniciar sessão e o apaga ao terminar.
- A `MainActivity` abre o detalhe da ocorrência quando se toca numa notificação (extra `incidentId`).
- O `firestore.rules` tem a coleção `deviceTokens`; na raiz há também `storage.rules` (versão de produção, não publicada, porque o Storage está em modo de teste), `firebase.json` e `.firebaserc`.

## Tarefas

### J1 — Início: 3 números do dashboard
- [x] Calcular, a partir de todas as ocorrências (`IncidentsList.getDashboardStats()`):
  - tempo médio de reparação, de Aberto a Encerrado (decisão 15);
  - número total de ocorrências, incluindo as privadas (decisão 16);
  - percentagem de ativas, ou seja, todas menos Encerrado (decisão 14).
- [x] Expor os valores com `MutableStateFlow` + `asStateFlow()` no `IncidentsViewModel` (`dashboardStats`).
- [x] Mostrar os 3 valores no `DashboardStatsCard`, no topo do `Dashboard`.
- [x] Destacar os valores num cartão com um tom suave da paleta azul-petróleo. A barra de topo partilhada usa a cor do ícone.
- **Feito:** testado no emulador a 11/11 (10,0 dias, 4 ocorrências, 75% ativas com os dados de exemplo).

### J2 — Lista com cartões expansíveis e filtro por perfil
- [x] Tocar no `IncidentCard` alterna a sua expansão. Categoria, situação e estado sempre visíveis e uma seta indica a expansão. A descrição e «Ver detalhes» aparecem ao expandir.
- [x] Botão na `MainTopAppBar` para expandir ou recolher todos os cartões da lista atual. Os IDs expandidos são guardados com `rememberSaveable`.
- [x] Realce visual do ícone quando todos os cartões estão expandidos e descrições de acessibilidade distintas em EN/PT para as ações do cartão e da barra.
- [x] Filtrar a lista com `canViewIncident` no `IncidentsViewModel`:
  - anónimo: públicas e não encerradas;
  - munícipe: as mesmas e as suas;
  - operador: todas.
- [x] Filtro "As minhas" para o munícipe: chips "Todas" / "As minhas" (`IncidentFilter`), estado `onlyMine` no `IncidentsViewModel`.
- [x] O botão «Ver detalhes» navega para `IncidentDetailRoute(incidentId)`.
- [x] Thumbnail da primeira foto no cartão (`IncidentPhoto`, 56 dp). Mostra um ícone quando a imagem está indisponível.
- **Feito quando:** cada perfil do seletor de sessão vê a lista certa. Já verificado no emulador: 2 ocorrências para o anónimo, 3 para o munícipe e 4 para o operador.

### Gráficos por estado e categoria (extra)
- [x] Dois gráficos na secção «Estatísticas» do início com o número de ocorrências visíveis por estado e categoria (`IncidentCharts`). O filtro «As minhas» também se aplica aos gráficos.

### J3 — Botão "Nova ocorrência"
- [x] Botão flutuante com ícone + e texto «Nova ocorrência», visível só para o munícipe (`canCreateIncident`).
- [x] Navega para `ReportIncidentRoute`.
- **Feito:** o botão aparece com o perfil Munícipe e não aparece com os outros dois.

### J4 — Detalhe da ocorrência
- [x] Ecrã stateless no estilo do `ShowScreen`, com tratamento de ocorrência inexistente (`IncidentDetailScreen`, `IncidentDetailUiState`, `IncidentDetailViewModel` + `Factory`).
- [x] Parte pública: categoria, situação, estado, data e descrição; sem dados do autor nem coordenadas.
- [x] Coordenadas e histórico de estados para o operador e o autor; dados do autor só para o operador.
- [x] Espaço para o `LifecycleActions` do Leonardo (`operatorActions`), visível só para o operador.
- [x] Parte do autor: reabrir (J8).
- [x] Pedidos e respostas no detalhe, visíveis ao autor e ao operador (J7).
- [x] Foto da ocorrência no topo do detalhe.
- **Feito quando:** os três perfis veem o mesmo ecrã com conteúdos diferentes. Verificado no emulador a 11/11 para o anónimo, o autor e o operador.
- [x] Mapa (aula #4, 16/11): `IncidentMap` com o pino exato e zoom próximo para o autor e o operador; zoom de cidade, sem coordenadas e sem gestos para os restantes (decisão 17).
- [x] Landscape: dados à esquerda, fotos e mapa à direita (padrão da P10).
- [x] Indicação de ocorrência de grupo (J6).

### J5 — Criar ocorrência
- [x] Ecrã com `ReportIncidentUiState` no estilo do `EditContactUiState`: campos, erro por campo e `isValid` (`ReportIncidentScreen`, `ReportIncidentViewModel` + `Factory`).
- [x] Campos: categoria (só as ativas) e situação em listas dropdown; descrição; visibilidade (pública ou privada) com `RadioButton`, como o registo do Leonardo.
- [x] Validação: todos os campos obrigatórios; os erros aparecem campo a campo ao submeter.
- [x] Gravar através do repositório e voltar ao início com `popBackStack()`; a lista e o dashboard atualizam-se.
- [x] Foto obrigatória, da galeria (Photo Picker) ou da câmara (`TakePicture` com `FileProvider`), como no Contacts das aulas.
- [x] Base de multimédia feita aqui: `utils/FileUtils`, `FileProvider` no manifesto, `res/xml/file_paths.xml`, Coil 3 e `IncidentPhoto`.
- **Feito:** testado no emulador a 11/11 com uma foto da câmara; a ocorrência nova aparece na lista com thumbnail e o dashboard passa a 5 ocorrências e 80% ativas.
- [x] Ponto obrigatório no mapa, escolhido pelo munícipe. O formulário mostra as coordenadas e mantém a escolha quando se junta ou troca a foto (decisão 12, revista a 16/11).
- [x] A foto de uma ocorrência abandonada é apagada (`onCleared`) e um erro de gravação aparece no formulário.
- [x] Landscape: foto e localização à esquerda, formulário à direita.
- [x] Vídeo opcional: passou para o Leonardo e foi feito a 20/11 (pontos 13 e A fechados).

### J7 — Pedidos de informação e mensagens
- [x] `InformationRequests`, componente stateless partilhado pelo autor e pelo operador no detalhe.
- [x] Pedido do operador disponível em Aberto e Em Resolução. Guarda o estado atual em `previousStatus` e passa a `INFO_REQUESTED`.
- [x] Pedido do munícipe disponível ao autor em qualquer estado exceto Encerrado. Mantém o estado (decisões 24 e E).
- [x] Resposta só em texto. A primeira resposta da outra parte marca o pedido como respondido. Se o pedido foi do operador, a ocorrência volta a `previousStatus` e limpa esse campo (decisões C e D).
- [x] Pedidos, respostas e mudanças de estado gravados com a ocorrência no repositório local.
- [x] Testado no emulador com o autor e o operador. Verificadas as restrições para o anónimo e para ocorrências encerradas, bem como a persistência após reiniciar.
- [x] Notificações dos pedidos e das respostas: feitas com a L11 (17/11). O teste em dois dispositivos está na L11 do Leonardo.

### J8 — Reabertura
- [x] Botão "Reabrir" no detalhe, visível só para o autor, em Resolvido e até 5 dias depois de `resolvedAt` (`canReopen`), com o prazo por baixo (`reopenDeadline`).
- [x] Justificação e foto obrigatórias num `AlertDialog`; o botão de confirmar só fica ativo com as duas. A foto escolhe-se com o `PhotoPicker` (galeria ou câmara) e é apagada se o utilizador cancelar.
- [x] Passa a Aberto, apaga o `resolvedAt`, incrementa o `reopenCount`, junta a foto (`MediaPurpose.REOPEN`) e acrescenta um `StatusChange` com a justificação como nota (`Incident.reopened()` em `data/model/IncidentActions.kt`; decisão 26).
- [x] O detalhe mostra todas as fotos numa fila horizontal.
- [x] O histórico mostra a nota de cada mudança.
- **Feito:** testado no emulador a 11/11 com a "Água" e, já com foto da câmara, com a "Iluminação" do `citizen-1`.

## Anotado — tarefas a criar depois da aula

| Tarefa | O que é | Aula |
|---|---|---|
| J10 | Deteção de impactos no eixo vertical com o acelerómetro (ponto 31 por confirmar) | passou para o Leonardo a 20/11 |

### J6 — Agrupamento a 10 m (16/11)
- [x] `IncidentsList.findGroupPrimary`: principal mais antiga, não encerrada, da mesma categoria, a menos de 10 m (`GROUP_RADIUS_METERS`); a nova guarda o `primaryIncidentId`.
- [x] Detalhe com o número de participações do grupo (`groupSize`).
- Testado: nova ocorrência a 2 m da "Vias Públicas" ficou ligada a ela.
- Ponto B fechado a 20/11: se a principal estiver encerrada, a nova passa a ser uma nova principal.

### J9 — Restrição dos 10 km (16/11)
- [x] `AccessRules.anonymousAccess` com `ANONYMOUS_RADIUS_METERS` à volta de `CITY_CENTER`.
- [x] Fora do raio, sem permissão ou a localizar: mensagem em vez do dashboard e da lista; o seletor de perfil continua (faz de login até à L1).
- Testado com `adb emu geo fix`: Coimbra mostra a lista; Lisboa bloqueia.

### Mapa de ocorrências e navegação (16/11, da app Contactos)
- [x] `IncidentsMapScreen` para todos os perfis com acesso: pinos das ocorrências visíveis, balão que abre o detalhe; câmara ajustada aos pinos só quando o perfil vê todas as localizações exatas.
- [x] `IncidentDirections` no detalhe, só para o operador: distância e botão «Navegar».

### F1 — Landscape (16/11)
- [x] Início, detalhe e nova ocorrência com layout próprio em landscape (P10).

## Sempre, em todos os meus ecrãs

A revisão final destes pontos nos ecrãs do João passou para o Leonardo e foi feita a 20/11 (ver [Tarefas_Leonardo.md](Tarefas_Leonardo.md)).


- [x] Textos em `strings.xml`, em inglês e em `values-pt`.
- [x] Funciona em portrait e landscape.
- [x] Descrições de acessibilidade nos ícones e botões.
- [x] Preview com dados de exemplo.

## Pontos por confirmar que eram meus

17, 31, A e B — ver [CityFix_Decisoes.md](CityFix_Decisoes.md), secção 3. Passaram para o Leonardo a 20/11; ficaram todos fechados nesse dia.
