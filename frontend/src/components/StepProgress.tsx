import React from "react";
import { View, Text } from "react-native";

interface Step {
  label: string;
  key: string;
}

interface StepProgressProps {
  steps: Step[];
  currentStep: number;
}

export default function StepProgress({ steps, currentStep }: StepProgressProps) {
  return (
    <View className="px-5 py-4">
      <View className="flex-row items-center justify-between">
        {steps.map((step, index) => {
          const isCompleted = index < currentStep;
          const isCurrent = index === currentStep;
          const isLast = index === steps.length - 1;

          return (
            <React.Fragment key={step.key}>
              {/* Step Circle + Label */}
              <View className="items-center">
                <View
                  className={`w-8 h-8 rounded-full items-center justify-center ${
                    isCompleted
                      ? "bg-teal"
                      : isCurrent
                      ? "bg-navy"
                      : "bg-faint/30"
                  }`}
                >
                  <Text
                    className={`text-sm font-bold ${
                      isCompleted || isCurrent ? "text-white" : "text-faint"
                    }`}
                  >
                    {isCompleted ? "✓" : index + 1}
                  </Text>
                </View>
                <Text
                  className={`text-xs mt-1 ${
                    isCurrent ? "text-navy font-semibold" : "text-faint"
                  }`}
                >
                  {step.label}
                </Text>
              </View>

              {/* Connector Line */}
              {!isLast && (
                <View
                  className={`flex-1 h-0.5 mx-2 ${
                    isCompleted ? "bg-teal" : "bg-faint/30"
                  }`}
                />
              )}
            </React.Fragment>
          );
        })}
      </View>
    </View>
  );
}
