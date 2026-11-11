# CityFix — Regras de negócio

Todas as regras de negócio do CityFix num só sítio: o que a app tem de fazer, onde está no código e de onde vem.
As decisões completas e os pontos por confirmar estão em [CityFix_Decisoes.md](CityFix_Decisoes.md); o enunciado é [AMov.ProjetoKotlin_PT.pdf](AMov.ProjetoKotlin_PT.pdf).

**Legenda do estado:** ✅ implementada · 📝 decidida, por implementar · ❓ depende de um ponto por confirmar

**Como manter este ficheiro:**
- Uma regra nova (do enunciado ou de uma decisão) entra com o próximo número da sua secção.
- Quando é implementada, passa a ✅ e indica-se onde está no código.
- Os números não se reutilizam: uma regra que deixe de valer fica riscada (~~assim~~) com a data e o motivo.

Origem: `I.1.B` e afins são secções do enunciado; `D16` e afins são decisões de [CityFix_Decisoes.md](CityFix_Decisoes.md); `A` a `E` identificam decisões ou pontos por confirmar nesse documento.

## 1. Perfis e acesso

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-01 | O anónimo vê na lista só as ocorrências públicas e não encerradas. | ✅ | `AccessRules.canViewIncident` | I.1.B, D16 |
| RN-02 | O munícipe vê as mesmas que o anónimo e também todas as suas, incluindo privadas e encerradas. | ✅ | `AccessRules.canViewIncident` | I.2.E |
| RN-03 | O operador vê todas as ocorrências. | ✅ | `AccessRules.canViewIncident` | I.3.E |
| RN-04 | No detalhe, quem não é autor nem operador vê categoria, situação, estado, data, descrição e fotos; não vê o autor nem as coordenadas. | ✅ | `IncidentDetailViewModel.refresh` | I.1.C |
| RN-05 | As coordenadas exatas e o histórico de estados aparecem ao operador e ao autor. | ✅ | `AccessRules.canSeeExactLocation`, `canSeeHistory` | D (11/11) |
| RN-06 | Os dados do autor (nome, e-mail, telefone) aparecem só ao operador. | ✅ | `AccessRules.canSeeAuthorData` | I.3.E |
| RN-07 | Uma ocorrência que o perfil não pode ver aparece como "não encontrada". | ✅ | `IncidentDetailViewModel.refresh` | RN-01 a RN-03 |
| RN-08 | Só o munícipe cria ocorrências. | ✅ | `AccessRules.canCreateIncident` (botão +) | I.2.E |
| RN-09 | Só o operador gere categorias e valida munícipes. | ✅ | `AccessRules.canManageCategories`, `canValidateCitizens` (botões da barra de topo) | I.3.D, I.3.E |
| RN-10 | O botão de registo aparece só a quem não tem sessão. | ✅ | `MainActivity` (`showRegister`) | I.2.A, I.3.A |
| RN-11 | As ações de gestão do ciclo de vida destinam-se ao operador. As mudanças na resposta a um pedido e na reabertura seguem RN-57 e RN-60. | 📝 | `AccessRules.canChangeStatus` (regra pronta; falta a L7) | I.3.E, II.2, II.4 |
| RN-12 | Um anónimo a mais de 10 km da Praça da República vê apenas o ecrã de login. | 📝 | J9 (aula #4); centro em `CityConstants.CITY_CENTER` | I.1, D3, D5 |
| RN-13 | Um munícipe pendente não inicia sessão e vê um aviso. | 📝 | L1; a sessão falsa já só escolhe contas ativas (`SessionViewModel`) | I.2.A, D6 |
| RN-14 | Só o autor e o operador consultam os pedidos e as respostas no detalhe da ocorrência. | ✅ | `AccessRules.canSeeInformationRequests`, `IncidentDetailScreen` | I.2.E, I.3.E, D (11/11) |

## 2. Registo, validação e perfil

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-20 | O nome é obrigatório. | ✅ | `Validators.nameError` | I.2.A |
| RN-21 | O e-mail tem de ter formato válido; o do operador tem de terminar em `@aac-coimbra.pt`. | ✅ | `Validators.emailError` | I.3.A, D9 |
| RN-22 | O telefone tem 9 dígitos e começa por 2 ou 9. | ✅ | `Validators.phoneError` | I.2.A |
| RN-23 | O NIF tem 9 dígitos e é único por perfil (o mesmo NIF pode existir num munícipe e num operador). | ✅ | `Validators.nifError`, `UsersList.hasNif` | I.2.A, I.3.A |
| RN-24 | O munícipe registado fica pendente; o operador fica logo ativo. | ✅ | `RegisterViewModel.register` | I.2.A, I.3.A |
| RN-25 | A opção de deteção de impactos só existe para o munícipe. | ✅ | `RegisterScreen` | I.2.A |
| RN-26 | O operador ativa um munícipe pendente, vendo o nome, a georreferenciação e o NIF. | ✅ | `CitizenValidationViewModel.activateCitizen` | I.3.D |
| RN-27 | No perfil não se alteram o nome nem o NIF; o munícipe também não altera a georreferenciação. | 📝 ❓ | L4; conflito 11 | I.2.C, I.3.C |

## 3. Categorias

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-30 | O nome da categoria é obrigatório. | ✅ | `EditCategoryViewModel.saveCategory` | I.3.E |
| RN-31 | O operador cria, edita, ativa e desativa categorias. | ✅ | `CategoriesViewModel`, `EditCategoryViewModel` | I.3.E |
| RN-32 | Na criação de ocorrências só aparecem as categorias ativas. | ✅ | `CategoriesList.getActiveSorted`, `ReportIncidentViewModel` | I.3.E |
| RN-33 | A ocorrência guarda uma cópia do nome da categoria, para o manter se a categoria for editada. | ✅ | `Incident.categoryName` | D (modelo) |

## 4. Criar ocorrência

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-40 | Categoria, situação, descrição e foto são obrigatórias. | ✅ | `ReportIncidentViewModel.saveIncident` | I.2.E, D12 |
| RN-41 | A visibilidade é pública por omissão e pode ser privada. | ✅ | `ReportIncidentUiState` | I.2.E |
| RN-42 | Uma ocorrência nova começa em Aberto. | ✅ | `Incident.status` (valor por omissão) | II.1 |
| RN-43 | A foto da criação vem da galeria ou da câmara e é guardada com o propósito `REPORT`. | ✅ | `PhotoPicker`, `ReportIncidentViewModel` | I.2.E, D13 |
| RN-44 | Provisório: a localização é a georreferenciação do autor ou, se não existir, o centro do município. | ✅ | `ReportIncidentViewModel`, `CityConstants.CITY_CENTER` | D (11/11) |
| RN-45 | A localização é obtida por GPS quando se tira a foto. Substitui a RN-44. | 📝 | J5 (aula #4) | D12 |
| RN-46 | Um vídeo pode acompanhar ou substituir a foto. | ❓ | J5 | D13, ponto A |
| RN-47 | Uma ocorrência nova a menos de 10 m de uma principal não encerrada da mesma categoria liga-se a ela; a primeira é a principal; as encerradas não contam. | 📝 ❓ | J6 (aula #4) | I.2, D18–D22, ponto B |

## 5. Ciclo de vida

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-50 | Os estados são Aberto, Pedido de informações, Em resolução, Resolvido e Encerrado. | ✅ | `IncidentStatus` | II |
| RN-51 | Uma ocorrência está ativa em qualquer estado exceto Encerrado. | ✅ | `IncidentsList.getDashboardStats` | D14 |
| RN-52 | Cada mudança de estado fica no histórico, com data, quem a fez e uma nota opcional. | ✅ na reabertura e nos pedidos, 📝 nas outras | `IncidentActions`; L7, L11 | I.3.E |
| RN-53 | O início dos trabalhos é a passagem a Em resolução e preenche `workStartedAt`. | 📝 | L7 | I.3.E, D27 |
| RN-54 | Resolver exige foto ou vídeo comprovativo, preenche `resolvedAt` e notifica o autor. | 📝 | L7, L11 | II.4 |
| RN-55 | Cinco dias depois de resolvida, a ocorrência passa a Encerrada e deixa de aceitar alterações. | 📝 ❓ | L11; ponto 28 | II.5 |
| RN-56 | O autor é avisado 24 h antes do encerramento. | 📝 | L11 | III |
| RN-57 | O operador pode pedir informação em Aberto ou Em resolução. O pedido guarda o estado atual em `previousStatus` e passa a Pedido de informações. A primeira resposta do autor repõe o estado anterior e limpa `previousStatus`. | ✅ | `AccessRules.canRequestInformation`, `IncidentActions.withInformationRequest`, `IncidentActions.withInformationReply` | II.2, D23, D24, C, D (11/11) |
| RN-58 | O munícipe pode pedir informação numa ocorrência da sua autoria em qualquer estado exceto Encerrado. O pedido mantém o estado. | ✅ | `AccessRules.canRequestInformation`, `IncidentActions.withInformationRequest` | D24, E |
| RN-59 | As mensagens estão associadas a pedidos de informação e as respostas são apenas em texto. A primeira resposta da outra parte marca o pedido como respondido. | ✅ | `AccessRules.canReplyToInformationRequest`, `IncidentDetailViewModel.replyToInformationRequest`, `IncidentActions.withInformationReply` | D25, C, D |

## 6. Reabertura

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-60 | Só o autor reabre, só em Resolvido e só até 5 dias depois de `resolvedAt`; o prazo aparece no detalhe. | ✅ | `AccessRules.canReopen`, `reopenDeadline` | II.4 |
| RN-61 | Uma ocorrência encerrada não se reabre. | ✅ | `AccessRules.canReopen` | II.5 |
| RN-62 | A justificação é obrigatória e fica como nota da mudança de estado. | ✅ | `ReopenDialog`, `Incident.reopened` | II.4 |
| RN-63 | A foto é obrigatória, da galeria ou da câmara, e junta-se às fotos da ocorrência com o propósito `REOPEN`. | ✅ | `ReopenDialog`, `Incident.reopened` | D13 (alterada a 11/11) |
| RN-64 | Ao reabrir, a ocorrência volta a Aberto, o `resolvedAt` passa a `null` e o `reopenCount` aumenta 1. | ✅ | `Incident.reopened` | D26, D (11/11) |
| RN-65 | Não há limite de reaberturas. | ✅ | `Incident.reopened` | D26 |

## 7. Dashboard

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-70 | Os três números são calculados sobre todas as ocorrências, incluindo as privadas, e são iguais para todos os perfis. | ✅ | `IncidentsList.getDashboardStats` | I.1.A, D16 |
| RN-71 | O tempo médio de reparação é a média de `closedAt − createdAt` das ocorrências encerradas; sem nenhuma, mostra "—". | ✅ ❓ | `IncidentsList.getDashboardStats`, `DashboardStatsCard` | D15, conflito 15 |
| RN-72 | O total conta todas as ocorrências. | ✅ | `IncidentsList.getDashboardStats` | I.1.A |
| RN-73 | A percentagem de ativas é ativas / total × 100 (RN-51). | ✅ | `IncidentsList.getDashboardStats` | I.1.A, D14 |
| RN-74 | O gráfico por categoria conta as ocorrências que o perfil vê na lista. | ✅ | `Dashboard` (`groupingBy`) | D (11/11) |
| RN-75 | O munícipe pode limitar a lista e o gráfico às suas ocorrências ("As minhas"). | ✅ | `IncidentsViewModel.setOnlyMine` | I.2.E |

## 8. Proximidade, sensores e notificações

| ID | Regra | Estado | Onde | Origem |
|---|---|---|---|---|
| RN-80 | Quando um operador está a menos de 20 m de uma ocorrência ativa, os autores são avisados. | 📝 ❓ | L10; pontos 29, 30 | I.3, II.3 |
| RN-81 | Há notificações para mudança de estado, mensagens, proximidade e aviso de 24 h. | 📝 ❓ | L11; ponto 32 | III |
| RN-82 | Com a deteção de impactos ativa, uma aceleração vertical brusca sugere abrir uma ocorrência de pavimento. | 📝 ❓ | J10; ponto 31 | I.2.F |
