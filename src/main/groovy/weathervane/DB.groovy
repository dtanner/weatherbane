package weathervane

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import groovy.sql.Sql

import java.sql.Timestamp
import java.time.Instant

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
            insert into prediction(id, predicted_on, target_date, location, provider, high, low, pop, response_id)
            values (:id, :predictedOn, :targetDate, :location, :provider, :high, :low, :pop, :responseId)
            """

        predictions.each { Prediction prediction ->

            sql.execute(query, [id         : UUID.randomUUID(),
                                predictedOn: new Timestamp(prediction.predictedOn.toEpochMilli()),
                                responseId : prediction.responseId,
                                targetDate : java.sql.Date.valueOf(prediction.targetDate),
                                location   : prediction.location,
                                provider   : prediction.provider,
                                high       : prediction.high,
                                low        : prediction.low,
                                pop        : prediction.pop])
        }

    }

    static UUID storeResponse(String provider,
                              int responseCode,
                              String responseText) {
        Sql sql = new Sql(DB.instance.ds)

        String query = """
            insert into prediction_response(id, created_timestamp, provider, response_code, response_text)
            values (:id, :createdTimestamp, :provider, :responseCode, cast(:responseText as jsonb))
            """

        def insertedIds = sql.executeInsert(query, [id              : UUID.randomUUID(),
                                                    createdTimestamp: new Timestamp(Instant.now().toEpochMilli()),
                                                    provider        : provider,
                                                    responseCode    : responseCode,
                                                    responseText    : responseText])

        return insertedIds[0][0] as UUID
    }
}
