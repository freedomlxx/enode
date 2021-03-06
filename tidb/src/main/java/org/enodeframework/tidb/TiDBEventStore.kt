package org.enodeframework.tidb

import org.enodeframework.common.serializing.ISerializeService
import org.enodeframework.eventing.IEventSerializer
import org.enodeframework.jdbc.DBConfiguration
import org.enodeframework.jdbc.JDBCEventStore
import java.util.regex.Pattern
import javax.sql.DataSource

/**
 * @author anruence@gmail.com
 */
class TiDBEventStore(dataSource: DataSource, setting: DBConfiguration, eventSerializer: IEventSerializer, serializeService: ISerializeService) : JDBCEventStore(dataSource, setting, eventSerializer, serializeService) {

    companion object {
        private val PATTERN_TIDB = Pattern.compile("^Duplicate entry '.*-(.*)' for key")
    }

    override fun getDuplicatedId(throwable: Throwable): String {
        val matcher = PATTERN_TIDB.matcher(throwable.message!!)
        if (!matcher.find()) {
            return ""
        }
        return if (matcher.groupCount() == 0) {
            ""
        } else matcher.group(1)
    }
}