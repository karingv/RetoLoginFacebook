package pe.edu.utp.consumoserviciosclima.modelo

class Ciudad(name: String, weather: ArrayList<Weather>, main: Main) {

    var name: String = ""
    var weather: ArrayList<Weather>? = null
    var main: Main? = null

    init {
        this.name = name
        this.weather = weather
        this.main = main
    }

}