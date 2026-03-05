# TODO - Checklist de Produção (VaaS)

Este documento foi criado para guiar e orientar os próximos desenvolvedores que atuarão no projeto **Validation as a Service (VaaS)**. A fundação de arquitetura, banco de dados relacional e design de componentes baseados no Quarkus já está sólida. 

Abaixo estão os débitos e funcionalidades essenciais que precisam ser priorizados e implementados antes de levar este projeto para o ambiente de Produção.

## 1. Implementação Padrão das Regras de Validação (Business Logic)
Atualmente as estratégias de validações herdam a interface `Validation`, mas grande parte possui apenas stubs. Precisam ser codificadas as integrações reais  para as seguintes classes do pacote `br.gov.serpro.vaas.validation`:

- [ ] **FacialValidation**: Lógica para comparar matematicamente features de duas imagens de face em Base64.
- [ ] **FacialWithLivenessValidation**: Integração com prova de vida (Anti-Spoofing).
- [ ] **DigitalValidation**: Análise de compatibilidade de minutiae de impressão digital.
- [ ] **QRCodeValidation**: Parse e decodificação do QRCode seguro do GovBR/CNH para atestar a validade de uma documentação.

## 2. Segurança - Autenticação e Autorização
- [ ] Implmentar uso so PLIN.

## 3. Telemetria e Observabilidade
- [ ] Adicionar logs.
- [ ] Habilitar o pacote Micrometer Prometheus (`quarkus-micrometer-registry-prometheus`) para injetar o endpoint nativo `/q/metrics`.

## 4. Integração e Entrega Contínua (CI/CD)
O projeto carece de pipelines para deploys automatizados:
- [ ] Criar arquivo `.gitlab-ci.yml`.



