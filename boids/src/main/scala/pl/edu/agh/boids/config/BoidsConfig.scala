package pl.edu.agh.boids.config

import pl.edu.agh.xinuk.config.{GuiType, XinukConfig}

final case class BoidsConfig(
                             gridSize: Int,
                             guiCellSize: Int,
                             signalSuppressionFactor: Double,
                             signalAttenuationFactor: Double,
                             workersRoot: Int,
                             shardingMod: Int,

                             guiType: GuiType,
                             isSupervisor: Boolean,
                             signalSpeedRatio: Int,
                             iterationsNumber: Long) extends XinukConfig
