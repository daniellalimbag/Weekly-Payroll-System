object DailyWorkRecordFactory {
    fun createNormalRecord(payrollConfig: PayrollConfig): DailyWorkRecord {
        return DailyWorkRecordBuilder(payrollConfig)
            .setDayType("Normal")
            .calculateShiftData()
            .build()
    }

    fun createRestDayRecord(payrollConfig: PayrollConfig): DailyWorkRecord {
        return DailyWorkRecordBuilder(payrollConfig)
            .setDayType("Rest Day")
            .setIsRestDay(true)
            .calculateShiftData()
            .build()
    }

    fun createRegularHolidayRecord(payrollConfig: PayrollConfig): DailyWorkRecord {
        return DailyWorkRecordBuilder(payrollConfig)
            .setDayType("Regular Holiday")
            .calculateShiftData()
            .build()
    }

    fun createSpecialNonWorkingDayRecord(payrollConfig: PayrollConfig): DailyWorkRecord {
        return DailyWorkRecordBuilder(payrollConfig)
            .setDayType("Special Non-Working Day")
            .calculateShiftData()
            .build()
    }

    fun createSpecialNonWorkingRestDayRecord(payrollConfig: PayrollConfig): DailyWorkRecord {
        return DailyWorkRecordBuilder(payrollConfig)
            .setDayType("Special Non-Working Day and Rest Day")
            .setIsRestDay(true)
            .calculateShiftData()
            .build()
    }

    fun createRegularHolidayRestDayRecord(payrollConfig: PayrollConfig): DailyWorkRecord {
        return DailyWorkRecordBuilder(payrollConfig)
            .setDayType("Regular Holiday and Rest Day")
            .setIsRestDay(true)
            .calculateShiftData()
            .build()
    }
}