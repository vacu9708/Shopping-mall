# 1
개발을 공부하면서 많은 것들을 해봤지만, 다양한 기술이 쓰이고 설계가 복잡하고 좋은 개발 방법론이 쓰인 "backend" 프로젝트는 해본 적이 없다. 즉, 해본 것들의 양만 많고 질이 낮다. 이게 현재 나의 최대 약점인 것 같다.<br>
이 약점을 극복하기 위해 실제로 쓸 수 있는 복잡한 제품을 출시해보면 좋겠지만 이건 창업의 수준이므로 현실적으로 힘들 것 같다. 다양한 기술이 쓰이고 설계가 복잡한 대표적인 backend 프로젝트는 쇼핑몰이다. Microservices architecture로 구성된 쇼핑몰을 실제 기업에서 개발하는 것처럼 만들어봐야겠다.<br>

# 2
처음해보는 게 많아서 막막하긴 했는데 우선 쇼핑몰이 어떤 마이크로서비스로 구성될 것인지 생각해봤다.<br>
유저 관리, 제품 재고 리스트, 제품 진열장, 주문 관리, 결제, 그리고 요청을 알맞은 서비스로 연결할 API gateway가 쇼핑몰에 필요한 최소한의 필수적인 서비스들일 것이다. 그 외의 서비스들은 나중에 추가해야겠다.<br>
필요한 마이크로 서비스들을 정리한 후에, 실제 구현을 위해 무슨 기술이 필요할지 정리했다. 필요한 기술 중에 ELK는 좀 감이 안잡히는데 나중에 공부하다보면 깨달을 거라고 생각한다.<br>

# 3
- Spring과 MySQL를 연동해 query 테스트를 해봤다. 이유는 모르겠는데 save()가 작동하지 않아서 해결 방법을 찾아다니다가 포기하고 raw SQL과 transaction commit을 했다. (나중에 원인을 알았는데 JPA만 있고 hibernate 설치가 안돼있었다)
- request - response가 잘 되는지 확인하기 위한 e2e test 연습을 했다.

# 4
- JWT 발급 방법이 node.js에선 쉬웠지만 spring에선 좀 이해가 안됐다. 계속 방법을 찾아봤고 성공했는데 node.js의 JWT보단 코드가 길지만 별로 복잡하진 않았다.
- API documentation 자동화를 위해 swagger를 쓰려고 했는데 기존에 인기 많았던 library가 더 이상 작동이 언됐다. 그래서 spring restDocs로 시도해봤는데 하루종일 해봐도 html문서가 안만들어져서 포기했고, swagger springdoc가 많이 쓰인다는 걸 알게 된 후 드디어 이걸로 API 문서가 생성된 걸 확인했다.

# 5
Redis에 관한 지식, Spring에서 Redis 사용법을 공부했고 어떤 데이터를 Redis에 저장하면 좋을지 고민했다. 쇼핑카트와 wishlist는 일시적인 데이터니까 caching하기 좋을 것이다.<br>
이 프로젝트에서 처음 copilot을 써봤는데, 정말 편리하고 생산성 향상이 크다.

# 6
Refresh token은 일시적이면서 빠른 토큰 재발급 속도가 필요하기 때문에 Redis에 저장하기 딱 좋아보였는데, 예전에 이해했다고 생각했던 Refresh token이 이해가 안됐다.<br>
Refresh token에 관한 지식과 access token 재발급 과정 등을 다시 공부했다.<br>

# 7
브랜칭 전략과 CI/CD에 관해 고민했다. 개인 프로젝트이기 때문에 복잡한 git-flow보다는 단순한 branch 전략이 더 좋아 보이기 때문에 Main - Topic 전략으로 했다.<br>
CI/CD는 예전에 했던 방식대로 github webhook, jenkins, AWS EC2를 조합했다.

# 8
graphQL에 대해 알아보면서 graphQL을 쓸지 말지 고민해봤는데 REST API를 써도 개발엔 지장이 없고 아직 필요성을 못 느끼겠어서 안 쓰기로 했다.<br>
Kafka를 docker에 설치했고 kafka의 기본적인 내용을 공부했다. 그리고 Spring cloud gateway에 들어온 요청을 kafka를 이용해 마이크로서비스1에 보낸 후, kafka broker의 topic에 publish해서 마이크로서비스2로 전송해보는 테스트를 해보았다.

# 9
Kubernetes가 Netflix eureka와 ribbon을 대체할 수 있다는 걸 알게돼서 나중에 load balancing을 위해 kubernetes를 쓰기로 했다.
AWS S3 테스트를 했고 잘됐다. 그리고 AWS RDS를 써야할지 고민했는데, 일단은 필요없으니까 나중에 필요할 때 도입해도 될 것 같다.

# 10
ER diagram과 class diagram의 초기 버전을 완성했고, 분산 시스템에서 transaction을 어떻게 할 지 고민했다.
주문 받기 -> authorization -> 결제 처리 -> 재고 감소 -> 주문 추가 -> email 알림(kafka) -> http response로 주문 처리가 이루어질 것인데, transaction에서 실패가 발생하면 failed를 http response로 보내야되기 때문에 kafka의 decoupling은 맞지 않다고 생각해 http로 transaction을 하기로 했다.
email 알림을 order management에 포함하지 않고 별도의 마이크로서비스로 분리했다. 대규모 트래픽에선 수많은 email을 보내야될 수도 있으니까 마이크로서비스로 만들고 kafka로 통신하는게 효율적일 것이라고 생각했기 때문이다.

# 11
마이크로서비스들이 서로 통신할 때 non-blocking이어야 대규모 트래픽에서도 버틸 수 있다고 생각해서 webflux를 사용했다.
AWS S3와의 통신은 CompletableFuture를 사용해서 non-blocking하게 만들었다.
CI/CD와 AWS S3의 diagram을 만들었고 아키텍처를 다듬었다. 그리고 postman을 이용해 API 테스트와 documentation을 했다.
