package weathervane

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import groovy.sql.Sql

import java.sql.Timestamp

@Singleton(lazy = true, strict = false)
class DB {

    HikariDataSource ds

    private DB() {
        ConfigObject appConfig = AppConfig.config

        HikariConfig config = new HikariConfig()
        config.jdbcUrl = appConfig.db.url
        config.username = appConfig.db.username
        config.password = appConfig.db.password
        config.driverClassName = "org.postgresql.Driver"

        ds = new HikariDataSource(config)
    }

    static void storePredictions(List<Prediction> predictions) {
        Sql sql = new Sql(DB.instance.ds)

        String query = """
            insert into prediction(id, predicted_on, target_date, location, provider, high, low, pop)
            values (:id, :predictedOn, :targetDate, :location, :provider, :high, :low, :pop)
            """

        predictions.each { Prediction prediction ->

            sql.execute(query, [id         : UUID.randomUUID(),
                                predictedOn: new Timestamp(prediction.predictedOn.toEpochMilli()),
                                targetDate : java.sql.Date.valueOf(prediction.targetDate),
                                location   : prediction.location,
                                provider   : prediction.provider,
                                high       : prediction.high,
                                low        : prediction.low,
                                pop        : prediction.pop])
        }

    }
}
