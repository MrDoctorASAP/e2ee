```java
CREATE USER user_name   
    [   
        { FOR | FROM } LOGIN login_name   
    ]  
    [ WITH <limited_options_list> [ ,... ] ]   
[ ; ]  
```

```java
CREATE USER dbUser
    FOR LOGIN dbUser
    WITH DEFAULT_SCHEMA = dbo;
    
```
