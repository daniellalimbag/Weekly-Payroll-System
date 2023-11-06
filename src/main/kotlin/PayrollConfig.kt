data class PayrollConfig(
    var dailyRate: Float = 500.0F,
    var maxHours: Int = 0,
    var workdays: Int = 2,
    var defDayType: String = "Normal",
    var defInTime: String = "0900",
    var defOutTime: String = "0900"
) {
    // Initializer block
    init {

    }
}
