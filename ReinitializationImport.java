// Simcenter STAR-CCM+ macro: ReinitializationImport.java
// Uses CSV file from ReinitializationExport.java or any CSV file to provide
// general initial condition settings for a sim.
// STAR-CCM+ Version: 2506 Build 19.04.007

package macro;

import java.util.*;
import star.common.*;
import star.base.neo.*;
import star.turbulence.*;
import star.flow.*;
import star.energy.*;

public class ReinitializationImport extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation sim = getActiveSimulation();

    // Set the file path here
    String initializationFile = "Initialization.csv";

    FileTable table = (FileTable) sim.getTableManager()
      .createFromFile(resolvePath(initializationFile), null);

    PhysicsContinuum phys = (PhysicsContinuum) sim.getContinuumManager()
      .getContinuum("Continuum Name");

    // Pressure
    InitialPressureProfile pressure = phys.getInitialConditions().get(InitialPressureProfile.class);
    pressure.setMethod(XyzTabularScalarProfileMethod.class);
    pressure.getMethod(XyzTabularScalarProfileMethod.class).setTable(table);
    pressure.getMethod(XyzTabularScalarProfileMethod.class).setData("Pressure");

    // Temperature
    StaticTemperatureProfile temp = phys.getInitialConditions().get(StaticTemperatureProfile.class);
    temp.setMethod(XyzTabularScalarProfileMethod.class);
    temp.getMethod(XyzTabularScalarProfileMethod.class).setTable(table);
    temp.getMethod(XyzTabularScalarProfileMethod.class).setData("Temperature");

    // Turbulent Viscosity Ratio
    TurbulentViscosityRatioProfile turbVisc = phys.getInitialConditions().get(TurbulentViscosityRatioProfile.class);
    turbVisc.setMethod(XyzTabularScalarProfileMethod.class);
    turbVisc.getMethod(XyzTabularScalarProfileMethod.class).setTable(table);
    turbVisc.getMethod(XyzTabularScalarProfileMethod.class).setData("Turbulent Viscosity Ratio");

    // Velocity (vector)
    VelocityProfile vel = phys.getInitialConditions().get(VelocityProfile.class);
    String[] labels = { "Velocity[i]", "Velocity[j]", "Velocity[k]" };

    for (int i = 0; i < 3; i++) {
      ScalarProfile comp = vel.getMethod(CompositeVectorProfileMethod.class).getProfile(i);
      comp.setMethod(XyzTabularScalarProfileMethod.class);
      comp.getMethod(XyzTabularScalarProfileMethod.class).setTable(table);
      comp.getMethod(XyzTabularScalarProfileMethod.class).setData(labels[i]);
    }
  }
}
