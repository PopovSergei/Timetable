package ru.ac.uniyar.database.repository

import ru.ac.uniyar.domain.group.Groups
import ru.ac.uniyar.domain.schedule.Schedule
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.user.Users
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import java.time.DayOfWeek
import java.util.UUID


class SchedulesDB: BaseTable() {
    private fun isUUID(uuid: String): Boolean {
        return try {
            UUID.fromString(uuid)
            true
        } catch (e: IllegalArgumentException) {
            false
        } catch (e: NullPointerException) {
            false
        }
    }
    private fun isDayOfWeekCorrect(dayOfWeek: String?): Boolean {
        try {
            if (dayOfWeek != null) {
                DayOfWeek.valueOf(dayOfWeek)
            } else {
                return false
            }
        } catch (e: IllegalArgumentException) {
            return false
        }
        return true
    }
    private fun isGroupIdCorrect(groups: Groups, groupId: String?): Boolean {
        return groups.fetchString(groupId) != null
    }

    @Throws(SQLException::class)
    fun selectSchedule(scheduleId: String, users: Users, groups: Groups): Schedule? {
        var schedule: Schedule? = null

        reopenConnection()
        val sqlStatement = "SELECT * FROM schedules WHERE id = ?"
        val statement: PreparedStatement = connection!!.prepareStatement(sqlStatement)
        statement.setString(1, scheduleId)
        statement.executeQuery()

        val result = statement.resultSet
        if (result != null) {
            val columns = result.metaData.columnCount
            while (result.next()) {
                if (isUUID(result.getString(1))
                    && isGroupIdCorrect(groups, result.getString(2))
                    && isDayOfWeekCorrect(result.getString(3))
                    && columns == 9)
                    schedule= (Schedule(
                        UUID.fromString(result.getString(1)),
                        groups.fetchString(result.getString(2))!!,
                        DayOfWeek.valueOf(result.getString(3)),
                        result.getInt(4), //classNumber
                        result.getString(5), //type
                        result.getString(6), //className
                        users.fetchString(result.getString(7)),
                        result.getString(8), //fractionClassName
                        users.fetchString(result.getString(9))
                    ))
            }
        }
        statement.close()
        return schedule
    }

    @Throws(SQLException::class)
    fun addSchedule(schedule: Schedule) {
        reopenConnection()
        val sqlStatement = "INSERT INTO schedules (id, groupId, dayOfWeek, classNumber, " +
                "type, className, teacherId, fractionClassName, fractionTeacherId) VALUES (?,?,?,?,?,?,?,?,?)"
        val statement: PreparedStatement = connection!!.prepareStatement(sqlStatement)
        statement.setString(1, schedule.id.toString())
        statement.setString(2, schedule.group.id.toString())
        statement.setString(3, schedule.dayOfWeek.toString())
        statement.setInt(4, schedule.classNumber)
        statement.setString(5, schedule.type)
        statement.setString(6, schedule.className)

        if (schedule.teacher == null) {
            statement.setString(7, null)
        } else {
            statement.setString(7, schedule.teacher.id.toString())
        }

        statement.setString(8, schedule.fractionClassName)

        if (schedule.fractionTeacher == null) {
            statement.setString(9, null)
        } else {
            statement.setString(9, schedule.fractionTeacher.id.toString())
        }

        statement.executeUpdate()
        statement.close()
    }

    @Throws(SQLException::class)
    fun updateSchedule(schedule: Schedule) {
        reopenConnection()
        val sqlStatement = "UPDATE schedules SET type = ?, className = ?, teacherId = ?, fractionClassName = ?, fractionTeacherId = ?  WHERE id = ?"
        val statement: PreparedStatement = connection!!.prepareStatement(sqlStatement)
        statement.setString(1, schedule.type)
        statement.setString(2, schedule.className)

        if (schedule.teacher == null) {
            statement.setString(3, null)
        } else {
            statement.setString(3, schedule.teacher.id.toString())
        }

        statement.setString(4, schedule.fractionClassName)

        if (schedule.fractionTeacher == null) {
            statement.setString(5, null)
        } else {
            statement.setString(5, schedule.fractionTeacher.id.toString())
        }

        statement.setString(6, schedule.id.toString())
        statement.executeUpdate()
        statement.close()
    }

    @Throws(SQLException::class)
    fun deleteSchedule(scheduleId: String) {
        reopenConnection()
        val sqlStatement = "DELETE FROM schedules WHERE id = ?"
        val statement: PreparedStatement = connection!!.prepareStatement(sqlStatement)
        statement.setString(1, scheduleId)
        statement.executeUpdate()
        statement.close()
    }

    @Throws(SQLException::class)
    fun initSchedules(schedules: Schedules, users: Users, groups: Groups) {
        reopenConnection()
        val statement: Statement = connection!!.createStatement()
        statement.executeQuery("SELECT * FROM schedules")

        val result = statement.resultSet
        if (result != null) {
            val columns = result.metaData.columnCount
            while (result.next()) {
                if (isUUID(result.getString(1))
                    && isGroupIdCorrect(groups, result.getString(2))
                    && isDayOfWeekCorrect(result.getString(3))
                    && columns == 9)
                    schedules.init(Schedule(
                        UUID.fromString(result.getString(1)),
                        groups.fetchString(result.getString(2))!!,
                        DayOfWeek.valueOf(result.getString(3)),
                        result.getInt(4), //classNumber
                        result.getString(5), //type
                        result.getString(6), //className
                        users.fetchString(result.getString(7)),
                        result.getString(8), //fractionClassName
                        users.fetchString(result.getString(9))
                    ))
            }
        }
        statement.close()
    }

    @Throws(SQLException::class)
    fun initTable() {
        //1
        super.executeSqlStatement("INSERT INTO schedules (id, groupId, dayOfWeek, classNumber, type, className, teacherId, fractionClassName, fractionTeacherId) " +
                "VALUES (" +
                "'e73d570e-e9b2-429e-9323-7e3c155658c2', " +
                "'492456eb-8822-4ab0-b47e-b9cdac773b12', " +
                "'MONDAY', " +
                "1, " +
                "'static', " +
                "'Математика, 220', " +
                "'7eb066da-06b7-4702-8106-29b4171cf5ae', " +
                "'', " +
                "null" +
                ")")
        //2
        super.executeSqlStatement("INSERT INTO schedules (id, groupId, dayOfWeek, classNumber, type, className, teacherId, fractionClassName, fractionTeacherId) " +
                "VALUES (" +
                "'45e2ddfd-440c-4532-9082-2041a733ccd9', " +
                "'492456eb-8822-4ab0-b47e-b9cdac773b12', " +
                "'MONDAY', " +
                "3, " +
                "'static', " +
                "'Базы данных, 210', " +
                "'4bed1b03-9f2d-44f9-b11a-fdd10bffd41e', " +
                "'', " +
                "null" +
                ")")
        //3
        super.executeSqlStatement("INSERT INTO schedules (id, groupId, dayOfWeek, classNumber, type, className, teacherId, fractionClassName, fractionTeacherId) " +
                "VALUES (" +
                "'9bf1765b-928b-4096-9942-67d5da36c53f', " +
                "'492456eb-8822-4ab0-b47e-b9cdac773b12', " +
                "'MONDAY', " +
                "2, " +
                "'static', " +
                "'Информатика, 216', " +
                "'4bed1b03-9f2d-44f9-b11a-fdd10bffd41e', " +
                "'', " +
                "null" +
                ")")
        //4
        super.executeSqlStatement("INSERT INTO schedules (id, groupId, dayOfWeek, classNumber, type, className, teacherId, fractionClassName, fractionTeacherId) " +
                "VALUES (" +
                "'da719038-87b1-45af-807b-1ce59e87e6b6', " +
                "'492456eb-8822-4ab0-b47e-b9cdac773b12', " +
                "'MONDAY', " +
                "4, " +
                "'fraction', " +
                "'Информатика, 216', " +
                "'4bed1b03-9f2d-44f9-b11a-fdd10bffd41e', " +
                "'Базы данных, 210', " +
                "'7eb066da-06b7-4702-8106-29b4171cf5ae'" +
                ")")
        //5
        super.executeSqlStatement("INSERT INTO schedules (id, groupId, dayOfWeek, classNumber, type, className, teacherId, fractionClassName, fractionTeacherId) " +
                "VALUES (" +
                "'0506a6e1-3271-4e18-a690-efca538cd968', " +
                "'97db0393-5c16-4280-a861-8514db7d6a87', " +
                "'MONDAY', " +
                "2, " +
                "'static', " +
                "'Математика, 215', " +
                "'7eb066da-06b7-4702-8106-29b4171cf5ae', " +
                "'', " +
                "null" +
                ")")

        //6
        super.executeSqlStatement("INSERT INTO schedules (id, groupId, dayOfWeek, classNumber, type, className, teacherId, fractionClassName, fractionTeacherId) " +
                "VALUES (" +
                "'d0dc86ef-73bd-4894-8a71-b58348c373fd', " +
                "'492456eb-8822-4ab0-b47e-b9cdac773b12', " +
                "'TUESDAY', " +
                "1, " +
                "'static', " +
                "'Математика, 220', " +
                "'7eb066da-06b7-4702-8106-29b4171cf5ae', " +
                "'', " +
                "null" +
                ")")
        //7
        super.executeSqlStatement("INSERT INTO schedules (id, groupId, dayOfWeek, classNumber, type, className, teacherId, fractionClassName, fractionTeacherId) " +
                "VALUES (" +
                "'627ecabb-0add-45ad-b74a-b42afd2c8f97', " +
                "'492456eb-8822-4ab0-b47e-b9cdac773b12', " +
                "'THURSDAY', " +
                "1, " +
                "'static', " +
                "'Информатика, 210', " +
                "'4bed1b03-9f2d-44f9-b11a-fdd10bffd41e', " +
                "'', " +
                "null" +
                ")")
        //8
        super.executeSqlStatement("INSERT INTO schedules (id, groupId, dayOfWeek, classNumber, type, className, teacherId, fractionClassName, fractionTeacherId) " +
                "VALUES (" +
                "'887183ae-82a6-478c-b96f-8d6b3c69db1c', " +
                "'492456eb-8822-4ab0-b47e-b9cdac773b12', " +
                "'WEDNESDAY', " +
                "1, " +
                "'static', " +
                "'Математика, 219', " +
                "'7eb066da-06b7-4702-8106-29b4171cf5ae', " +
                "'', " +
                "null" +
                ")")
        //9
        super.executeSqlStatement("INSERT INTO schedules (id, groupId, dayOfWeek, classNumber, type, className, teacherId, fractionClassName, fractionTeacherId) " +
                "VALUES (" +
                "'e19b9d94-d856-4ee6-8de6-b65249ca56f9', " +
                "'492456eb-8822-4ab0-b47e-b9cdac773b12', " +
                "'FRIDAY', " +
                "1, " +
                "'static', " +
                "'Информатика, 216', " +
                "'4bed1b03-9f2d-44f9-b11a-fdd10bffd41e', " +
                "'', " +
                "null" +
                ")")
        //10
        super.executeSqlStatement("INSERT INTO schedules (id, groupId, dayOfWeek, classNumber, type, className, teacherId, fractionClassName, fractionTeacherId) " +
                "VALUES (" +
                "'b7524685-8b8e-40fb-bae1-4a02495d2eff', " +
                "'492456eb-8822-4ab0-b47e-b9cdac773b12', " +
                "'SATURDAY', " +
                "1, " +
                "'static', " +
                "'Математика, 220', " +
                "'7eb066da-06b7-4702-8106-29b4171cf5ae', " +
                "'', " +
                "null" +
                ")")
    }

    @Throws(SQLException::class)
    fun createTable() {
        super.executeSqlStatement(
            "CREATE TABLE IF NOT EXISTS schedules " +
                    "( " +
                    "id VARCHAR(64) NOT NULL PRIMARY KEY, " +
                    "groupId VARCHAR(64) NOT NULL, " +
                    "dayOfWeek VARCHAR(32) NOT NULL, " +
                    "classNumber INT NOT NULL, " +
                    "type VARCHAR(32) NOT NULL, " +
                    "className VARCHAR(255), " +
                    "teacherId VARCHAR(64), " +
                    "fractionClassName VARCHAR(255), " +
                    "fractionTeacherId VARCHAR(64)" +
                    ")"
        )
        super.executeSqlStatement("ALTER TABLE schedules ADD FOREIGN KEY (groupId) REFERENCES groups(id) ON UPDATE CASCADE")
        super.executeSqlStatement("ALTER TABLE schedules ADD FOREIGN KEY (teacherId) REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL")
        super.executeSqlStatement("ALTER TABLE schedules ADD FOREIGN KEY (fractionTeacherId) REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL")
    }

    @Throws(SQLException::class)
    fun dropTable() {
        super.executeSqlStatement("DROP TABLE IF EXISTS schedules")
    }
}