object DailyWorkRecordFactory {
    // Creates a normal workday record
    fun createNormalRecord(payrollConfig: PayrollConfig): DailyWorkRecord {
        return DailyWorkRecordBuilder(payrollConfig)
            .setDayType("Normal")
            .calculateShiftData()
            .build()
    }

    // Creates a rest day record
    fun createRestDayRecord(payrollConfig: PayrollConfig): DailyWorkRecord {
        return DailyWorkRecordBuilder(payrollConfig)
            .setDayType("Rest Day")
            .setIsRestDay(true)
            .calculateShiftData()
            .build()
    }

    // Creates a regular holiday record
    fun createRegularHolidayRecord(payrollConfig: PayrollConfig): DailyWorkRecord {
        return DailyWorkRecordBuilder(payrollConfig)
            .setDayType("Regular Holiday")
            .calculateShiftData()
            .build()
    }

    // Creates a special non-working day record
    fun createSpecialNonWorkingDayRecord(payrollConfig: PayrollConfig): DailyWorkRecord {
        return DailyWorkRecordBuilder(payrollConfig)
            .setDayType("Special Non-Working Day")
            .calculateShiftData()
            .build()
    }

    // Creates a combined record for a special non-working day that is also a rest day
    fun createSpecialNonWorkingRestDayRecord(payrollConfig: PayrollConfig): DailyWorkRecord {
        return DailyWorkRecordBuilder(payrollConfig)
            .setDayType("Special Non-Working Day and Rest Day")
            .setIsRestDay(true)
            .calculateShiftData()
            .build()
    }

    // Creates a combined record for a regular holiday that is also a rest day
    fun createRegularHolidayRestDayRecord(payrollConfig: PayrollConfig): DailyWorkRecord {
        return DailyWorkRecordBuilder(payrollConfig)
            .setDayType("Regular Holiday and Rest Day")
            .setIsRestDay(true)
            .calculateShiftData()
            .build()
    }
}