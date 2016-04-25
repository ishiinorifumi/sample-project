package jp.co.disney.spplogin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

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
}
