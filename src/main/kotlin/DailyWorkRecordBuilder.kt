class DailyWorkRecordBuilder(private val payrollConfig: PayrollConfig) {
    private var inTime: String = payrollConfig.defInTime
    private var outTime: String = payrollConfig.defOutTime
    private var dayType: String = payrollConfig.defDayType
    private var regOvertimeHrs: Float = 0.0F
    private var nsHrs: Float = 0.0F
    private var nsOvertimeHrs: Float = 0.0F
    private var salary: Float = 0.0F
    private var isNightShift: Boolean = false
    private var isAbsent: Boolean = false
    private var isRestDay: Boolean = false

    fun setInTime(time: String) = apply { this.inTime = time }
    fun setOutTime(time: String) = apply { this.outTime = time }
    fun setDayType(type: String) = apply { this.dayType = type }
    fun setIsRestDay(restDay: Boolean) = apply { this.isRestDay = restDay }

    fun setOvertime(regHrs: Float, nsHrs: Float, nsOvertimeHrs: Float) = apply {
        this.regOvertimeHrs = regHrs
        this.nsHrs = nsHrs
        this.nsOvertimeHrs = nsOvertimeHrs
    }

    fun calculateShiftData(): DailyWorkRecordBuilder = apply {
        val nInTime = inTime.toInt()
        var nOutTime = outTime.toInt()
        if (nOutTime < nInTime) nOutTime += 2400

        isAbsent = nInTime == nOutTime
        isNightShift = nOutTime >= 2200

        val workHours = payrollConfig.maxHours * 100 + 100
        val isOvertime = nOutTime > (nInTime + workHours)

        if (isNightShift && isOvertime) {
            val rMinutes = subtractMilitaryTimes(2200, (nInTime + workHours))
            regOvertimeHrs = rMinutes / 60.0F
            nsHrs = 0.0F
            nsOvertimeHrs = subtractMilitaryTimes(nOutTime, 2200) / 60.0F
        } else if (isNightShift) {
            nsHrs = subtractMilitaryTimes(nOutTime, 2200) / 60.0F
        } else if (isOvertime) {
            regOvertimeHrs = subtractMilitaryTimes(nOutTime, (nInTime + workHours)) / 60.0F
        }
    }

    fun build(): DailyWorkRecord {
        return DailyWorkRecord(
            payrollConfig = payrollConfig,
            inTime = inTime,
            outTime = outTime,
            dayType = dayType,
            regOvertimeHrs = regOvertimeHrs,
            nsHrs = nsHrs,
            nsOvertimeHrs = nsOvertimeHrs,
            salary = salary,
            isNightShift = isNightShift,
            isAbsent = isAbsent,
            isRestDay = isRestDay
        )
    }

    private fun subtractMilitaryTimes(time1: Int, time2: Int): Int {
        val hours1 = time1 / 100
        val minutes1 = time1 % 100
        val hours2 = time2 / 100
        val minutes2 = time2 % 100
        val totalMinutes1 = hours1 * 60 + minutes1
        val totalMinutes2 = hours2 * 60 + minutes2
        return totalMinutes1 - totalMinutes2
    }
}
