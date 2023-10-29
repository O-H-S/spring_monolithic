
흔히 ID/Password 라고 하지만, 
스프링 시큐리티의 Principal에서는 Name/Password 인 듯하다.

즉, name이 사용자의 고유한 식별자가 된다.

또한,
UserDetails에서는 Name -> Username 으로 명칭이 바뀐다.
OAuth2User에서는 Name -> Name 으로 동일하다.

