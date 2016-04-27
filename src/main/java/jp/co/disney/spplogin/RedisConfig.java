package jp.co.disney.spplogin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import jp.co.disney.spplogin.web.model.Guest;

@Configuration
@EnableRedisHttpSession
public class RedisConfig {

	/**
	 * セキュアなRedis環境では設定変更コマンド発行が無効化されているため、EnableRedisHttpSessionによる
	 * Redisの自動設定処理時にエラーが発生する。このためSpringによる自動設定処理を無効化する必要がある。
	 * @return
	 */
    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }
    
    @Bean
    public RedisTemplate<String, Guest> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Guest> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Guest.class));
        redisTemplate.setHashKeySerializer(redisTemplate.getKeySerializer());
        redisTemplate.setHashValueSerializer(redisTemplate.getValueSerializer());
        return redisTemplate;
    }
}
