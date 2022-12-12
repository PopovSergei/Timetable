package ru.ac.uniyar.domain

import ru.ac.uniyar.database.repository.GroupsDB
import ru.ac.uniyar.database.repository.SchedulesDB
import ru.ac.uniyar.database.repository.UsersDB
import ru.ac.uniyar.domain.group.Groups
import ru.ac.uniyar.domain.schedule.Schedules
import ru.ac.uniyar.domain.user.Users
import java.sql.SQLException

class InitData {
    fun initData(
        users: Users,
        schedules: Schedules,
        groups: Groups,
        usersDB: UsersDB,
        groupsDB: GroupsDB,
        schedulesDB: SchedulesDB,
    ) {
        try {
//            schedulesDB.dropTable()
//            usersDB.dropTable()
//            groupsDB.dropTable()
//
//            usersDB.createTable()
//            groupsDB.createTable()
//            schedulesDB.createTable()
//
//            usersDB.initTable()
//            groupsDB.initTable()
//            schedulesDB.initTable()

            usersDB.initUsers(users)
            groupsDB.initGroups(groups)
            schedulesDB.initSchedules(schedules, users, groups)
        } catch (e: SQLException) {
            e.printStackTrace()
            println("SQL Error!")
        }
    }
}