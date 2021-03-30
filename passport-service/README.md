需要生成非对称密码来支持JWT
keytool -genkey -alias ikeyi -keyalg RSA -keysize 2048 -keystore jwt.jks