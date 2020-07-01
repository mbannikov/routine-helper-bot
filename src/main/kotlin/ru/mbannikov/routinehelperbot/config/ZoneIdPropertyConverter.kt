package ru.mbannikov.routinehelperbot.config

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import java.time.ZoneId

@Component
@ConfigurationPropertiesBinding
class ZoneIdPropertyConverter : Converter<String, ZoneId> {
    override fun convert(from: String): ZoneId = ZoneId.of(from)
}
