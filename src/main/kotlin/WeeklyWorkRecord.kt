data class WeeklyWorkRecord(
    var records: MutableList<DailyWorkRecord> = ArrayList(7),
    var totalSalary: Float = 0.0f
)
