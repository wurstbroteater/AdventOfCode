import javax.sound.sampled.FloatControl;

public class Seed {
    private Long id;
    private Long soil;
    private Long fertilizer;
    private Long water;
    private Long light;
    private Long temperature;
    private Long humidity;
    private Long location;

    public Seed(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getSoil() {
        return soil;
    }

    public Long getFertilizer() {
        return fertilizer;
    }

    public Long getWater() {
        return water;
    }

    public Long getLight() {
        return light;
    }

    public Long getTemperature() {
        return temperature;
    }

    public Long getHumidity() {
        return humidity;
    }

    public Long getLocation() {
        return location;
    }

    public void setValueByMapName(final String name, final long value) {
        switch (name) {
            case "seed-to-soil" -> soil = value;
            case "soil-to-fertilizer" -> fertilizer = value;
            case "fertilizer-to-water" -> water = value;
            case "water-to-light" -> light = value;
            case "light-to-temperature" -> temperature = value;
            case "temperature-to-humidity" -> humidity = value;
            case "humidity-to-location" -> location = value;
            default -> throw new IllegalArgumentException("Unable to find map for name: " + name);
        }
    }

    @Override
    public String toString() {
        return "Seed[" +
                "id=" + id +
                ", soil=" + soil +
                ", fertilizer=" + fertilizer +
                ", water=" + water +
                ", light=" + light +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", location=" + location +
                ']';
    }
}

