# Tarefas — João Carvalho

Plano geral em [CityFix_Plano.md](CityFix_Plano.md). Molde das aulas: ContactApp e ficha AMov.P07.

## Fase 0 — com o Leonardo

- [x] 0.1 e 0.2 (feitas pelo Leonardo).
- [x] 0.3 `utils/AccessRules.kt`.
- [x] 0.4 Sessão falsa: `SessionViewModel` e `SessionSelector`.
- [x] 0.5 `NavHost` único com `Scaffold` e `MainTopAppBar`; saíram o `Screen.JOAO` / `Screen.LEONARDO` e o `DevMenuScreen`.
- [ ] 0.6 Contratos do `LifecycleActions` e do componente de mensagens, com o Leonardo.
- [x] 0.7 Textos em `strings.xml` (EN e PT); os textos e as cores de teste do `MainScreen` saíram.

Descrição de cada uma em [CityFix_Plano.md](CityFix_Plano.md), secção 4.

## Tarefas

### J1 — Início: 3 números do dashboard
- [x] Calcular, a partir de todas as ocorrências (`IncidentsList.getDashboardStats()`):
  - tempo médio de reparação, de Aberto a Encerrado (decisão 15);
  - número total de ocorrências, incluindo as privadas (decisão 16);
  - percentagem de ativas, ou seja, todas menos Encerrado (decisão 14).
- [x] Expor os valores com `MutableStateFlow` + `asStateFlow()` no `IncidentsViewModel` (`dashboardStats`).
- [x] Mostrar os 3 valores no `DashboardStatsCard`, no topo do `Dashboard`.
- **Feito:** testado no emulador a 11/11 (10,0 dias, 4 ocorrências, 75% ativas com os dados de exemplo).

### J2 — Lista com cartões expansíveis e filtro por perfil
- [x] `IncidentCard` como o `ContactCard`: categoria e situação sempre visíveis (o enunciado pede a situação na lista); estado e descrição dentro do `AnimatedVisibility(isExpanded)`.
- [x] Botão na `MainTopAppBar` para alternar `isExpanded`, guardado com `rememberSaveable`.
- [x] Filtrar a lista com `canViewIncident` no `IncidentsViewModel`:
  - anónimo: públicas e não encerradas;
  - munícipe: as mesmas e as suas;
  - operador: todas.
- [x] Filtro "As minhas" para o munícipe: chips "Todas" / "As minhas" (`IncidentFilter`), estado `onlyMine` no `IncidentsViewModel`.
- [x] Clique num cartão navega para `IncidentDetailRoute(incidentId)`.
- [x] Thumbnail da primeira foto no cartão (`IncidentPhoto`, 56 dp); sem foto, mostra um ícone.
- **Feito quando:** cada perfil do seletor de sessão vê a lista certa. Já verificado no emulador: 2 ocorrências para o anónimo, 3 para o munícipe e 4 para o operador.

### Gráfico por categoria (extra)
- [x] Segunda página do `Dashboard` com o número de ocorrências por categoria (`IncidentChart`).

### J3 — Botão "Nova ocorrência"
- [x] Botão flutuante +, visível só para o munícipe (`canCreateIncident`).
- [x] Navega para `ReportIncidentRoute`.
- **Feito:** o botão aparece com o perfil Munícipe e não aparece com os outros dois.

### J4 — Detalhe da ocorrência
- [x] Ecrã stateless no estilo do `ShowScreen`, com tratamento de ocorrência inexistente (`IncidentDetailScreen`, `IncidentDetailUiState`, `IncidentDetailViewModel` + `Factory`).
- [x] Parte pública: categoria, situação, estado, data e descrição; sem dados do autor nem coordenadas.
- [x] Coordenadas e histórico de estados para o operador e o autor; dados do autor só para o operador.
- [x] Espaço para o `LifecycleActions` do Leonardo (`operatorActions`), visível só para o operador.
- [x] Parte do autor: reabrir (J8).
- [ ] Parte do autor: mensagens (J7).
- [x] Foto da ocorrência no topo do detalhe.
- **Feito quando:** os três perfis veem o mesmo ecrã com conteúdos diferentes. Verificado no emulador a 11/11 para o anónimo, o autor e o operador.
- 📝 **Anotado (#4):** mapa com zoom afastado para o anónimo e coordenadas exatas para o operador.

### J5 — Criar ocorrência
- [x] Ecrã com `ReportIncidentUiState` no estilo do `EditContactUiState`: campos, erro por campo e `isValid` (`ReportIncidentScreen`, `ReportIncidentViewModel` + `Factory`).
- [x] Campos: categoria (só as ativas), situação, descrição, visibilidade (pública ou privada), com `RadioButton` como o registo do Leonardo.
- [x] Validação: todos os campos obrigatórios; os erros aparecem campo a campo ao submeter.
- [x] Gravar através do repositório e voltar ao início com `popBackStack()`; a lista e o dashboard atualizam-se.
- [x] Foto obrigatória, da galeria (Photo Picker) ou da câmara (`TakePicture` com `FileProvider`), como no Contacts das aulas.
- [x] Base de multimédia feita aqui: `utils/FileUtils`, `FileProvider` no manifesto, `res/xml/file_paths.xml`, Coil 3 e `IncidentPhoto`.
- **Feito:** testado no emulador a 11/11 com uma foto da câmara; a ocorrência nova aparece na lista com thumbnail e o dashboard passa a 5 ocorrências e 80% ativas.
- 📝 **Provisório:** a localização é a do registo do autor ou o centro do município, até haver GPS.
- 📝 **Anotado (#4):** localização obtida quando se tira a foto (decisão 12).
- 📝 **Por definir:** vídeo (ponto A por confirmar).

### J7 — Pedidos de informação e mensagens
- [ ] Componente stateless de mensagens, com a assinatura combinada na tarefa 0.6.
- [ ] Usado no detalhe pelo autor e pelo operador.
- [ ] Pedido do operador: guarda o estado atual em `previousStatus` e passa a `INFO_REQUESTED` (decisões 23 e 24).
- [ ] Pedido do munícipe: não muda o estado (decisão 24).
- [ ] Resposta ao pedido do operador: o estado volta a `previousStatus`.
- **Feito quando:** um pedido do operador muda o estado e a resposta repõe-no.
- ❓ **Fechar antes:** pontos C, D e E de [CityFix_Decisoes.md](CityFix_Decisoes.md).

### J8 — Reabertura
- [x] Botão "Reabrir" no detalhe, visível só para o autor, em Resolvido e até 5 dias depois de `resolvedAt` (`canReopen`), com o prazo por baixo (`reopenDeadline`).
- [x] Justificação obrigatória num `AlertDialog`; o botão de confirmar só fica ativo com texto.
- [x] Passa a Aberto, apaga o `resolvedAt`, incrementa o `reopenCount` e acrescenta um `StatusChange` com a justificação como nota (`Incident.reopened()` em `data/model/IncidentActions.kt`; decisão 26).
- [x] O histórico mostra a nota de cada mudança.
- **Feito:** testado no emulador a 11/11 com a ocorrência "Água" do `citizen-1`.

## Anotado — tarefas a criar depois da aula

| Tarefa | O que é | Aula |
|---|---|---|
| J6 | Agrupamento: nova ocorrência liga-se à principal da mesma categoria a menos de 10 m (decisões 18 a 22; ponto B por confirmar) | #4 |
| J9 | Restrição dos 10 km à volta da Praça da República para anónimos (decisões 3 e 5) | #4 |
| J10 | Deteção de impactos no eixo vertical com o acelerómetro (ponto 31 por confirmar) | por definir |
| F1 | Layouts adaptativos em landscape nos meus ecrãs | #4 |

## Sempre, em todos os meus ecrãs

- [ ] Textos em `strings.xml`, em inglês e em `values-pt`.
- [ ] Funciona em portrait e landscape.
- [ ] Descrições de acessibilidade nos ícones e botões.
- [ ] Preview com dados de exemplo.

## Pontos por confirmar que são meus

17, 31, A, B, C, D, E — ver [CityFix_Decisoes.md](CityFix_Decisoes.md), secção 3.
