// Simcenter STAR-CCM+ macro: ReinitializationExport.java
// General macro to grab list of common fields used for reinitialization and store in a table to export.
// Use ReinitializationImport.java to quickly define this exported file as the initial conditions for a given sim.
// Take note to modify region name on line ~44
// STAR-CCM+ Version: 2506 Build 19.04.007

package macro;

import java.util.*;
import star.common.*;
import star.base.neo.*;
import star.vis.*;

public class ReinitializationExport extends StarMacro {

  public void execute() {
    execute0();
  }

  private void execute0() {

    Simulation sim = getActiveSimulation();

    XyzInternalTable xyzInternalTable_0 =
      sim.getTableManager().create("star.common.XyzInternalTable");

    // Dynamically collect field functions
    FieldFunctionManager ffm = sim.getFieldFunctionManager();
    List<FieldFunction> fieldFunctions = new ArrayList<>();

    String[] scalarFields = { "Pressure", "Temperature", "TurbulentViscosityRatio" };
    for (String name : scalarFields) {
      fieldFunctions.add((PrimitiveFieldFunction) ffm.getFunction(name));
    }

    PrimitiveFieldFunction velocity = (PrimitiveFieldFunction) ffm.getFunction("Velocity");
    for (int i = 0; i < 3; i++) {
      fieldFunctions.add(velocity.getComponentFunction(i));
    }

    xyzInternalTable_0.setFieldFunctions(fieldFunctions);

    // Get all region parts and boundaries
    Region region_0 = sim.getRegionManager().getRegion("inner region");
    List<NamedObject> partsList = new ArrayList<>();
    partsList.add(region_0);
    partsList.addAll(region_0.getBoundaryManager().getObjects());

    xyzInternalTable_0.getParts().setObjects(partsList);

    //xyzInternalTable_0.extract();

    //xyzInternalTable_0.export("./Initialization.csv", ",");

  }
}
