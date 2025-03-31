// Simcenter STAR-CCM+ macro: ReinitializationImport.java
// Uses CSV file from ReinitializationExport.java or any CSV file to provide
// general initial condition settings for a sim.
// Make note to modify useAllContinua flag if you want to only modify one or all
// continua. If set to 0, please specify the name in targetContinuumName.
// If set to 1, no continuum name needs to specified. (This will also work if you have one
// continuum, but do not want to reset the name across different simulations.
// STAR-CCM+ Version: 2506 Build 19.04.007

package macro;

import java.util.*;
import star.common.*;
import star.base.neo.*;
import star.turbulence.*;
import star.flow.*;
import star.energy.*;

public class ImportReinitialization extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation sim = getActiveSimulation();

    // === CONFIG FLAG ===
    int useAllContinua = 1; // 0 = one continuum, 1 = all
    String targetContinuumName = "ContinuumName";
    String initializationFile = "Initialization.csv";

    FileTable table = (FileTable) sim.getTableManager()
      .createFromFile(resolvePath(initializationFile), null);

    List<PhysicsContinuum> targets = new ArrayList<>();

    if (useAllContinua == 1) {
      for (Continuum cont : sim.getContinuumManager().getObjects()) {
        if (cont instanceof PhysicsContinuum) {
          targets.add((PhysicsContinuum) cont);
        }
      }
    } else {
      targets.add((PhysicsContinuum) sim.getContinuumManager().getContinuum(targetContinuumName));
    }

    for (PhysicsContinuum phys : targets) {
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

      // Velocity
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
}

