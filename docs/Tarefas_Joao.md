# Tarefas — João Carvalho

Plano geral em [CityFix_Plano.md](CityFix_Plano.md). Molde das aulas: ContactApp e ficha AMov.P07.

## Fase 0 — com o Leonardo

- [ ] 0.1 a 0.7, descritas em [CityFix_Plano.md](CityFix_Plano.md), secção 4.

## Tarefas

### J1 — Início: 3 números do dashboard
- [ ] Calcular no ViewModel do início, a partir da lista de ocorrências:
  - tempo médio de reparação, de Aberto a Encerrado (decisão 15);
  - número total de ocorrências, incluindo as privadas (decisão 16);
  - percentagem de ativas, ou seja, todas menos Encerrado (decisão 14).
- [ ] Expor os valores com `MutableStateFlow` + `asStateFlow()`, como o `ContactsViewModel`.
- [ ] Mostrar os 3 valores por cima da lista, no `Dashboard`.
- **Feito quando:** os números batem certo com os dados de exemplo e aparecem no preview e no emulador.

### J2 — Lista com cartões expansíveis e filtro por perfil
- [ ] `IncidentCard` como o `ContactCard`: categoria sempre visível; situação e estado dentro do `AnimatedVisibility(isExpanded)`.
- [ ] Botão na `MainTopAppBar` para alternar `isExpanded`, guardado com `rememberSaveable`.
- [ ] Filtrar a lista com as regras de `AccessRules.kt`:
  - anónimo: lista pública;
  - munícipe: lista pública e filtro "As minhas";
  - operador: todas.
- [ ] Clique num cartão navega para `IncidentDetailRoute(incidentId)`.
- **Feito quando:** cada perfil do seletor de sessão vê a lista certa.

### J3 — Botão "Nova ocorrência"
- [ ] Botão na barra de topo ou flutuante, visível só para o munícipe (regra em `AccessRules.kt`).
- [ ] Navega para `ReportIncidentRoute`.
- **Feito quando:** o botão aparece com o perfil Munícipe e desaparece com os outros dois.

### J4 — Detalhe da ocorrência
- [ ] Ecrã stateless no estilo do `ShowScreen`, com tratamento de ocorrência inexistente.
- [ ] Parte pública: categoria, situação, estado, descrição; sem dados do autor.
- [ ] Parte do autor: mensagens (J7) e reabrir (J8), visíveis só se o utilizador for o autor.
- [ ] Espaço para o `LifecycleActions` do Leonardo, visível só para o operador.
- **Feito quando:** os três perfis veem o mesmo ecrã com conteúdos diferentes.
- 📝 **Anotado (#4):** mapa com zoom afastado para o anónimo e coordenadas exatas para o operador.

### J5 — Criar ocorrência
- [ ] Ecrã com `ReportIncidentUiState` no estilo do `EditContactUiState`: campos, erro por campo e `isValid`.
- [ ] Campos: categoria (só as ativas), situação, descrição, visibilidade (pública ou privada).
- [ ] Validação: todos os campos obrigatórios.
- [ ] Gravar através do repositório e voltar ao início com `popBackStack()`.
- **Feito quando:** uma ocorrência criada aparece na lista.
- 📝 **Anotado (#3):** foto ou vídeo obrigatório (decisão 12; ponto A por confirmar).
- 📝 **Anotado (#4):** localização obtida quando se tira a foto (decisão 12).

### J7 — Pedidos de informação e mensagens
- [ ] Componente stateless de mensagens, com a assinatura combinada na tarefa 0.6.
- [ ] Usado no detalhe pelo autor e pelo operador.
- [ ] Pedido do operador: guarda o estado atual em `previousStatus` e passa a `INFO_REQUESTED` (decisões 23 e 24).
- [ ] Pedido do munícipe: não muda o estado (decisão 24).
- [ ] Resposta ao pedido do operador: o estado volta a `previousStatus`.
- **Feito quando:** um pedido do operador muda o estado e a resposta repõe-no.
- ❓ **Fechar antes:** pontos C, D e E de [CityFix_Decisoes.md](CityFix_Decisoes.md).

### J8 — Reabertura
- [ ] Botão "Reabrir" visível só para o autor, em Resolvido e até 5 dias depois de `resolvedAt`.
- [ ] Justificação obrigatória.
- [ ] Passa a Aberto, incrementa o contador de reaberturas e regista a mudança de estado (decisão 26).
- **Feito quando:** a reabertura funciona dentro dos 5 dias e o botão não aparece depois.

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
