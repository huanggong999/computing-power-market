<template>
	<up-tag
		:text="text"
		:type="type"
		:plain="plain"
		:plain-fill="plainFill"
		:shape="shape"
		:borderColor="borderColor"
		:size="size"
	/>
</template>

<script setup lang="ts">
type TagType = "info" | "success" | "warning" | "error" | "primary";
type TagShape = "square" | "circle";
withDefaults(
	defineProps<{
		text: string;
		type?: TagType;
		shape?: TagShape;
		plain?: boolean;
		plainFill?: boolean;
		borderColor?: string;
		size?: "medium" | "large" | "mini";
	}>(),
	{
		type: "info",
		shape: "square",
		plain: true,
		plainFill: false,
		size: "medium",
	}
);
</script>

<style lang="scss" scoped></style>
