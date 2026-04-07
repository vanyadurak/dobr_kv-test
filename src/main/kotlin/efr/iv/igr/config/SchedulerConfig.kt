package efr.iv.igr.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

@Configuration
class SchedulerConfig {
    @Bean
    fun jdbcScheduler(): Scheduler {
        return Schedulers.newBoundedElastic(10, 100, "jdbc-worker")
    }
}