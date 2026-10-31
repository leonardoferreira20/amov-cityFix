# Tarefas — Leonardo Ferreira

Plano geral em [CityFix_Plano.md](CityFix_Plano.md). Molde das aulas: ContactApp e ficha AMov.P07.

## Fase 0 — com o João

- [ ] 0.1 a 0.7, descritas em [CityFix_Plano.md](CityFix_Plano.md), secção 4.

## Tarefas

### L3 — Registo (munícipe e operador)
- [ ] Um só ecrã para os dois perfis, com `RegisterUiState` no estilo do `EditContactUiState`: campos, erro por campo e `isValid`.
- [ ] Campos comuns: nome, e-mail, telefone, NIF, perfil.
- [ ] Validações:
  - e-mail e telefone com expressão regular, como no ContactApp;
  - NIF com 9 dígitos e único para o mesmo perfil (verificado no repositório);
  - operador: e-mail do domínio `@aac-coimbra.pt` (decisão 9).
- [ ] Munícipe: opção "Deteção de Impactos"; conta fica `PENDING`.
- [ ] Operador: conta fica `ACTIVE`.
- [ ] Gravar o utilizador através do repositório.
- **Feito quando:** os dois perfis registam-se com dados válidos e os erros aparecem campo a campo.
- 📝 **Anotado (🔒 Firebase):** criação da conta no Firebase Authentication.
- 📝 **Anotado (#4):** georreferenciação do munícipe (ponto 8 por confirmar).

### L4 — Perfil
- [ ] Ecrã de edição no estilo do `EditScreen`, reaproveitando as validações do L3.
- [ ] Campos bloqueados conforme o enunciado: nome e NIF para os dois perfis; georreferenciação também para o munícipe.
- **Feito quando:** os campos bloqueados não se editam e as alterações ficam gravadas.
- ❓ **Fechar antes:** conflito 11 de [CityFix_Decisoes.md](CityFix_Decisoes.md) (nome e morada).
- 📝 **Anotado (#3):** foto de perfil.

### L5 — Validação de munícipes
- [ ] Ecrã só para o operador (regra em `AccessRules.kt`).
- [ ] Lista dos munícipes `PENDING` com nome, georreferenciação e NIF.
- [ ] Botão para ativar: passa a `ACTIVE`.
- **Feito quando:** um munícipe ativado sai da lista de pendentes.

### L6 — Categorias
- [ ] Lista de categorias, só para o operador.
- [ ] Um só ecrã para criar e editar, com `EditCategoryRoute(categoryId: String? = null)`, como o `EditContactRoute`.
- [ ] Ativar e desativar.
- [ ] Só as categorias ativas aparecem na criação de ocorrências (tarefa J5 do João).
- **Feito quando:** uma categoria desativada deixa de aparecer na criação de ocorrências.

### L7 — `LifecycleActions`
- [ ] Componente stateless com a assinatura combinada na tarefa 0.6, colocado no detalhe do João.
- [ ] Só mostra as transições válidas a partir do estado atual.
- [ ] Cada transição regista um `StatusChange` com timestamp, e o histórico aparece no detalhe.
- [ ] Pedido de informação do operador (combinar com a tarefa J7 do João).
- [ ] Início dos trabalhos: passa a `IN_PROGRESS` e preenche `workStartedAt` (decisão 27).
- [ ] Resolver: passa a `RESOLVED` e preenche `resolvedAt`.
- [ ] Encerrado não aceita alterações.
- **Feito quando:** o operador leva uma ocorrência de Aberto a Resolvido e o histórico mostra cada passo.
- 📝 **Anotado (#3):** foto ou vídeo comprovativo obrigatório ao resolver.

## Anotado — tarefas a criar depois da aula

| Tarefa | O que é | Aula |
|---|---|---|
| L1 | Login com e-mail e password; aviso de conta pendente (decisão 6) | Firebase |
| L2 | MFA e recuperação de password por e-mail (ponto 10 por confirmar) | Firebase |
| L8 | Multimédia: Photo Picker, câmara com `FileProvider`, imagens com Coil 3 (ponto 13 por confirmar) | #3 |
| L9 | Compressão, thumbnail e envio com WorkManager só com rede | por definir |
| L10 | Geofence de 20 m e alerta aos autores (pontos 29 e 30 por confirmar) | por definir |
| L11 | Notificações (estado, mensagens, proximidade, 24 h antes do fecho) e encerramento aos 5 dias (pontos 28 e 32 por confirmar) | Firebase / por definir |
| F1 | Layouts adaptativos em landscape nos meus ecrãs | #4 |

## Sempre, em todos os meus ecrãs

- [ ] Textos em `strings.xml`, em inglês e em `values-pt`.
- [ ] Funciona em portrait e landscape.
- [ ] Descrições de acessibilidade nos ícones e botões.
- [ ] Preview com dados de exemplo.

## Pontos por confirmar que são meus

7, 8, 10, 13, 28, 29, 30, 32 e o conflito 11 — ver [CityFix_Decisoes.md](CityFix_Decisoes.md), secções 2 e 3.
