package joe.parameter;

import com.bulletphysics.linearmath.Transform;
import composites.entities.Entity;
import joe.parameter.csv.BoatCsvData;
import joe.parameter.physics.IsloatedPhysics;
import physics.util.PhysTransform;
import renderables.r3D.rotation.Quat4fHelper;
import renderables.r3D.water.RenderedWater;

import javax.vecmath.*;
import java.io.File;

/**
 * Created by CaptainPete on 8/5/2018.
 */
public class DynamicsParametersFinder {

    public static final float PHYSICS_TIMESTEP = 1.0f / 50.0f;

    public static final float SETTLE_TIME = 10.0f;

    private RenderedWater water;

    //private BoatCsvData boatCsvData;

    public static void main(String... args) {
        new DynamicsParametersFinder();
    }

    private DynamicsParametersFinder(){
        String path = "res/data_example.csv";

        File csvFile = new File(path);

        BoatCsvData boatCsvDataLinear = new BoatCsvData(csvFile);
        BoatCsvData boatCsvDataAngular = new BoatCsvData(csvFile);

        this.water = new RenderedWater(null, null, null, null, null, -1, -1);

        float min_forwardThrustForceMultiplier = 0f;
        float min_backwardThrustForceMultiplier = 0f;
        float min_linearDamping = 0.0f;
        float min_angularDamping = 0.0f;

        float max_forwardThrustForceMultiplier = 300f;
        float max_backwardThrustForceMultiplier = 300f;
        float max_linearDamping = 1.0f;
        float max_angularDamping = 1.0f;

        float step_forwardThrustForceMultiplier = 90f;
        float step_backwardThrustForceMultiplier = 90f;
        float step_linearDamping = 0.25f;
        float step_angularDamping = 0.25f;

        float best_forwardThrustForceMultiplier = 100f;
        float best_backwardThrustForceMultiplier = 80f;
        float best_linearDamping = 0.3f;
        float best_angularDamping = 0.5f;

        float bestQualityMeasurement = 0f;

        float r2linearAtBest = 0f;
        float r2angularAtBest = 0f;
        //performTest(boatCsvDataAngular);


        //This a bit on the lame side, but it might work for now.
        for(float curr_forwardThrustForceMultiplier = min_forwardThrustForceMultiplier; curr_forwardThrustForceMultiplier <= max_forwardThrustForceMultiplier; curr_forwardThrustForceMultiplier += step_forwardThrustForceMultiplier){

            float numIterations = (max_forwardThrustForceMultiplier - min_forwardThrustForceMultiplier) / step_forwardThrustForceMultiplier;
            float elapsedIterations = (curr_forwardThrustForceMultiplier - min_forwardThrustForceMultiplier) / step_forwardThrustForceMultiplier;
            float percentComplete = elapsedIterations / numIterations;
            float percentComplete100 = percentComplete * 100.0f;
            System.out.println();
            System.out.println(percentComplete100 + "% complete.");

            for(float curr_backwardThrustForceMultiplier = min_backwardThrustForceMultiplier; curr_backwardThrustForceMultiplier <= max_backwardThrustForceMultiplier; curr_backwardThrustForceMultiplier += step_backwardThrustForceMultiplier){
                System.out.print("|");
                for(float curr_linearDamping = min_linearDamping; curr_linearDamping <= max_linearDamping; curr_linearDamping += step_linearDamping){
                    System.out.print(".");
                    for(float curr_angularDamping = min_angularDamping; curr_angularDamping <= max_angularDamping; curr_angularDamping += step_angularDamping){

                        performTest(boatCsvDataLinear, curr_forwardThrustForceMultiplier, curr_backwardThrustForceMultiplier, curr_linearDamping, curr_angularDamping);
                        performTest(boatCsvDataAngular, curr_forwardThrustForceMultiplier, curr_backwardThrustForceMultiplier, curr_linearDamping, curr_angularDamping);

                        float r2linear = boatCsvDataLinear.getRSquared(boatCsvDataLinear.getSpeeds(), boatCsvDataLinear.getSimSpeeds());
                        float r2angular = boatCsvDataLinear.getRSquared(boatCsvDataAngular.getHeadingRates(), boatCsvDataAngular.getHeadingRates());

                        float qualityMeasurement = r2linear * r2angular;

                        if(qualityMeasurement > bestQualityMeasurement){
                            bestQualityMeasurement = qualityMeasurement;
                            r2linearAtBest = r2linear;
                            r2angularAtBest = r2angular;

                            best_forwardThrustForceMultiplier = curr_forwardThrustForceMultiplier;
                            best_backwardThrustForceMultiplier = curr_backwardThrustForceMultiplier;
                            best_linearDamping = curr_linearDamping;
                            best_angularDamping = curr_angularDamping;

                        }
                    }
                }
            }

            System.out.println("bestQualityMeasurement = " + bestQualityMeasurement);
            System.out.println("r2linearAtBest = " + r2linearAtBest);
            System.out.println("r2angularAtBest = " + r2angularAtBest);
            System.out.println("best_forwardThrustForceMultiplier = " + best_forwardThrustForceMultiplier);
            System.out.println("best_backwardThrustForceMultiplier = " + best_backwardThrustForceMultiplier);
            System.out.println("best_linearDamping = " + best_linearDamping);
            System.out.println("best_angularDamping = " + best_angularDamping);
        }

        System.out.println();
        System.out.println("----------------------");
        System.out.println();

        System.out.println("bestQualityMeasurement = " + bestQualityMeasurement);
        System.out.println("r2linearAtBest = " + r2linearAtBest);
        System.out.println("r2angularAtBest = " + r2angularAtBest);
        System.out.println("best_forwardThrustForceMultiplier = " + best_forwardThrustForceMultiplier);
        System.out.println("best_backwardThrustForceMultiplier = " + best_backwardThrustForceMultiplier);
        System.out.println("best_linearDamping = " + best_linearDamping);
        System.out.println("best_angularDamping = " + best_angularDamping);


        System.out.println("100.00% Complete...");
    }


    private void performTest(BoatCsvData boatCsvData, float forwardThrustForceMultiplier, float backwardThrustForceMultiplier, float linearDamping, float angularDamping){

        IsloatedPhysics isloatedPhysics = new IsloatedPhysics(water, PHYSICS_TIMESTEP);
        isloatedPhysics.initPhysics();

        //Zero motor forces.
        isloatedPhysics.getWamV().applyConstantMotorForceOnLeftPontoon(0f);
        isloatedPhysics.getWamV().applyConstantMotorForceOnRightPontoon(0f);

        //Settle wamv.
        float timer = 0f;

        while (timer < SETTLE_TIME) {
            timer += PHYSICS_TIMESTEP;
            isloatedPhysics.updatePhysicsSingleStep(PHYSICS_TIMESTEP);

        }

        //We have now settled.



        //Determine initial settings:
        Transform initialWamVTransform = isloatedPhysics.getWamV().getWorldTransform();
        Vector3f initialPosition = PhysTransform.toGlPosition(initialWamVTransform.origin);

        Quat4f initialRotation = PhysTransform.toGlRotation(initialWamVTransform.basis);
        Vector3f initialXyzRotation = new Vector3f();
        Quat4fHelper.toXYZRotation(initialRotation, initialXyzRotation);
        float initialHeadingAngle = (float) Math.toDegrees(initialXyzRotation.x);


        //Create variables.
        Vector3f previousPosition = new Vector3f(initialPosition);
        Vector3f previousXyzRotation = new Vector3f(initialXyzRotation);

        timer = 0f;

        int csvIndex = 0;

        //Set changable variables.
        isloatedPhysics.getWamV().updateParameters(forwardThrustForceMultiplier, backwardThrustForceMultiplier, linearDamping, angularDamping);

        float timeBetweenDataPoint = 0f;

        boatCsvData.getSimTimes()[0] = 0f;
        boatCsvData.getSimSpeeds()[0] = 0f;
        boatCsvData.getSimHeadings()[0] = initialHeadingAngle;
        boatCsvData.getSimHeadingRates()[0] = 0f;
        boatCsvData.getSimLeftMotorPercentages()[0] = 0f;
        boatCsvData.getSimRightMotorPercentages()[0] = 0f;

        while (csvIndex < (boatCsvData.dataHeight() - 1)) {
            float currentLeftPercentage = boatCsvData.getLeftMotorPercentages()[csvIndex];
            float currentRightPercentage = boatCsvData.getRightMotorPercentages()[csvIndex];

            isloatedPhysics.getWamV().applyConstantMotorForceOnLeftPontoon(currentLeftPercentage);
            isloatedPhysics.getWamV().applyConstantMotorForceOnRightPontoon(currentRightPercentage);

            timer += PHYSICS_TIMESTEP;
            timeBetweenDataPoint += PHYSICS_TIMESTEP;
            isloatedPhysics.updatePhysicsSingleStep(PHYSICS_TIMESTEP);

            //Get current values.
            Transform wamVTransform = isloatedPhysics.getWamV().getWorldTransform();
            Vector3f currentPosition = PhysTransform.toGlPosition(wamVTransform.origin);

            Quat4f currentRotation = PhysTransform.toGlRotation(wamVTransform.basis);
            Vector3f currentXyzRotation = new Vector3f();
            Quat4fHelper.toXYZRotation(currentRotation, currentXyzRotation);
            float currentHeadingAngle = (float) Math.toDegrees(currentXyzRotation.x);

            float nextTimeStep = boatCsvData.getTimes()[csvIndex + 1];

            //Check if we've gone past the next data point:
            if (timer >= nextTimeStep) {
                //Move to the next time step.
                Vector3f travelledDistance = new Vector3f(currentPosition);
                travelledDistance.sub(previousPosition);
                previousPosition = new Vector3f(currentPosition);
                Vector2f xzMovement = new Vector2f(travelledDistance.x, travelledDistance.z);
                float distanceTravelled = xzMovement.length();
                float averageVelocity = distanceTravelled / timeBetweenDataPoint;


                float previousAngleDeg = (float) Math.toDegrees(previousXyzRotation.x);
                float angleDifference = currentHeadingAngle - previousAngleDeg;
                float headingRate = angleDifference / timeBetweenDataPoint;
                previousXyzRotation = new Vector3f(currentXyzRotation);

                //Store sim data for this run.
                boatCsvData.getSimTimes()[csvIndex + 1] = timer;
                boatCsvData.getSimSpeeds()[csvIndex + 1] = averageVelocity;
                boatCsvData.getSimHeadings()[csvIndex + 1] = currentHeadingAngle;
                boatCsvData.getSimHeadingRates()[csvIndex + 1] = headingRate;
                boatCsvData.getSimLeftMotorPercentages()[csvIndex + 1] = currentLeftPercentage;
                boatCsvData.getSimRightMotorPercentages()[csvIndex + 1] = currentRightPercentage;

                csvIndex++;
                timeBetweenDataPoint = 0f;
            }


        }

    }

    public static void setEntityTransform(Entity entity, Transform transform) {
        javax.vecmath.Vector3f glBoxPosition = PhysTransform.toGlPosition(transform.origin);
        setEntityPosition(entity, glBoxPosition);
        Quat4f glBoxRotation = PhysTransform.toGlRotation(transform.basis);
        setEntityRotation(entity, glBoxRotation);
    }

    public static void setEntityPosition(Entity entity, javax.vecmath.Vector3f position) {
        entity.setPosition(position.x, position.y, position.z);
    }

    public static void setEntitySize(Entity entity, javax.vecmath.Vector3f size) {
        entity.setSize(size.x, size.y, size.z);
    }

    public static void setEntityRotation(Entity entity, Tuple4f tuple4f) {
        entity.getQuatRotation().set(tuple4f);
        entity.getQuatRotation().inverse();
    }


}
