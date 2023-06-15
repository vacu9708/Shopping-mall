# 1
개발를 공부하면서 많은 것들을 해봤지만, 다양한 기술이 쓰이고 설계가 복잡하고 좋은 개발 방법론이 쓰인 "backend" 프로젝트는 해본 적이 없다. 즉, 해본 것들의 양만 많고 질이 낮다. 이게 현재 나의 최대 약점인 것 같다.<br>
이 약점을 극복하기 위해 실제로 쓸 수 있는 복잡한 제품을 출시해보면 좋겠지만 이건 창업의 수준이므로 현실적으로 힘들 것 같다. 다양한 기술이 쓰이고 설계가 복잡한 대표적인 backend 프로젝트는 쇼핑몰이다. Microservices architecture로 구성된 쇼핑몰을 실제 기업에서 개발하는 방식으로 만들어봐야겠다.<br>

# 2
처음해보는 게 많아서 막막하긴 했지만 우선 쇼핑몰이 어떤 마이크로서비스로 구성될 것인지 생각해봤다.<br>
유저 관리, 제품 재고 리스트, 제품 진열장, 주문 관리, 결제, 그리고 요청을 알맞은 서비스로 연결할 API gateway가 쇼핑몰에 필요한 최소한의 필수적인 서비스들일 것이다. 그 외의 서비스들은 나중에 추가해야겠다.<br>
필요한 마이크로 서비스들을 정리한 후에, 실제 구현을 위해 무슨 기술이 필요할지 정리했다. 필요한 기술 중에 ELK는 좀 감이 안잡히는데 나중에 공부하다보면 깨달을 거라고 생각한다.<br>

# 3
- Spring과 MySQL를 연동해 query 테스트를 해봤다. 이유는 모르겠는데 save()가 작동하지 않아서 해결 방법을 찾아다니다가 포기하고 raw SQL과 transaction commit을 했다.
- request - response가 잘 되는지 확인하기 위해 e2e test를 해봤다.

# 4
- JWT 발급 방법이 node.js에선 쉬웠지만 spring에선 좀 이해가 안됐다. 계속 방법을 찾아봤고 성공했는데 node.js의 JWT보단 코드가 길지만 별로 복잡하진 않았다.
- API documentation 자동화를 위해 swagger를 쓰려고 했는데 기존에 인기 많았던 library가 더 이상 작동이 언됐다. 그래서 spring restDocs로 시도해봤는데 하루종일 해봐도 html문서가 안만들어져서 포기했고, swagger springdoc가 많이 쓰인다는 걸 알게 된 후 드디어 이걸로 API 문서가 생성된 걸 확인했다.

# 5
Redis에 관한 지식, Spring에서 Redis 사용법를 공부했고 어떤 데이터를 Redis에 저장하면 좋을지 고민했다. 쇼핑카트와 wishlist는 일시적인 데이터니까 caching하기 좋을 것이다.<br>
이 프로젝트에서 처음 copilot을 써봤는데, 정말 편리하고 생산성 향상이 크다.

# 6
Refresh token은 일시적이면서 빠른 토큰 재발급 속도가 필요하기 때문에 Redis에 저장하기 딱 좋아보였는데, 예전에 이해했다고 생각했던 Refresh token이 이해가 안됐다.<br>
Refresh token에 관한 지식과 access token 재발급 과정 등을 다시 공부했다.<br>

# 7
Branching strategy와 CI/CD에 관해 고민했는데, 예전에 이해했다고 생각했던 CI/CD의 기본 지식이 헷갈려서 다시 공부했다.<br>
Branching strategy는, 개인 프로젝트이기 때문에 복잡한 git-flow보다는 단순한 branch 전략이 더 좋아 보이기 때문에 Main - Topic 전략으로 해야겠다.<br>
CI/CD는 예전에 했던 방식대로 github webhook, jenkins, AWS EC2를 조합하고, Topic branch에 CI trigger, Main branch에 CD trigger를 설정해야겠다.<br>
