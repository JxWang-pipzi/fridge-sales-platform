<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="rules"
    :label-width="labelWidth || '80px'"
    class="pro-form"
    :class="$attrs.class"
  >
    <template v-for="field in schema" :key="field.prop">
      <el-form-item 
        :label="field.label" 
        :prop="field.prop" 
        :rules="field.rules || (field.required ? [{ required: true, message: `${field.label} 是必填项`, trigger: 'blur' }] : [])"
        :class="{ 'full-width': field.fullWidth }"
      >
        <template v-if="field.slot">
          <slot :name="field.slot" :model="formData" :field="field"></slot>
        </template>
        <component
          v-else
          :is="getComponentType(field.type)"
          v-model="formData[field.prop]"
          v-bind="field.props"
          :placeholder="field.props?.placeholder || `请输入${field.label}`"
          :style="field.type === 'number' ? '' : 'width: 100%'"
        >
          <template v-if="field.options && field.type === 'select'">
            <el-option
              v-for="opt in field.options"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </template>
          <template v-if="field.options && field.type === 'radio'">
            <el-radio
              v-for="opt in field.options"
              :key="opt.value"
              :value="opt.value"
            >
              {{ opt.label }}
            </el-radio>
          </template>
           <template v-if="field.options && field.type === 'checkbox'">
            <el-checkbox
              v-for="opt in field.options"
              :key="opt.value"
              :value="opt.value"
              :label="opt.label"
            />
          </template>
        </component>
      </el-form-item>
    </template>
    
    <el-form-item>
      <slot name="actions" :submit="submit" :reset="reset">
        <el-button type="primary" @click="submit">确定</el-button>
        <el-button @click="reset">取消</el-button>
      </slot>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'

const props = defineProps({
  schema: {
    type: Array,
    required: true
  },
  modelValue: {
    type: Object,
    default: () => ({})
  },
  labelWidth: {
    type: String,
    default: '120px'
  }
})

const emit = defineEmits(['update:modelValue', 'submit', 'reset'])

const formRef = ref(null)
const formData = reactive({ ...props.modelValue })

watch(() => props.modelValue, (val) => {
  Object.assign(formData, val)
}, { deep: true })

watch(formData, (val) => {
  emit('update:modelValue', val)
}, { deep: true })

const rules = computed(() => {
  const rules = {}
  props.schema.forEach(field => {
    if (field.rules) {
      rules[field.prop] = field.rules
    } else if (field.required) {
      rules[field.prop] = [
        { required: true, message: `${field.label} 是必填项`, trigger: 'blur' }
      ]
    }
  })
  return rules
})

const getComponentType = (type) => {
  const componentMap = {
    'select': 'el-select',
    'date': 'el-date-picker',
    'switch': 'el-switch',
    'checkbox': 'el-checkbox-group',
    'radio': 'el-radio-group',
    'textarea': 'el-input',
    'number': 'el-input-number'
  }
  return componentMap[type] || 'el-input'
}

const submit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      emit('submit', formData)
    } else {
      console.log('error submit!', fields)
    }
  })
}

const reset = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
  emit('reset')
}

defineExpose({
  formRef,
  submit,
  reset
})
</script>

<style scoped>
/* 数字输入框样式修复 */
.pro-form :deep(.el-input-number) {
  width: auto !important;
  display: inline-block !important;
}

.pro-form :deep(.el-input-number .el-input-number__decrease),
.pro-form :deep(.el-input-number .el-input-number__increase) {
  width: 32px !important;
  height: 32px !important;
  line-height: 30px !important;
  font-size: 16px !important;
}

.pro-form :deep(.el-input-number .el-input-number__input) {
  width: 50px !important;
}

.pro-form :deep(.el-input-number .el-input-number__inner) {
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

/* Grid 布局样式 - 只在启用 grid-layout 类时生效 */
.pro-form.grid-layout {
  display: grid !important;
  grid-template-columns: repeat(2, 1fr) !important;
  gap: 16px 20px !important;
}

/* 独占一行的字段 */
.pro-form.grid-layout :deep(.el-form-item.full-width) {
  grid-column: span 2 !important;
}

/* Grid 布局时的表单项样式 */
.pro-form.grid-layout :deep(.el-form-item) {
  display: grid !important;
  grid-template-columns: 80px 1fr !important;
  align-items: center !important;
  margin-bottom: 0 !important;
}

/* Grid 布局时的标签样式 */
.pro-form.grid-layout :deep(.el-form-item__label) {
  text-align: right !important;
  padding-right: 12px !important;
  white-space: nowrap !important;
  display: flex !important;
  align-items: center !important;
  justify-content: flex-end !important;
  width: 80px !important;
  min-width: 80px !important;
  max-width: 80px !important;
}

/* Grid 布局时的内容样式 */
.pro-form.grid-layout :deep(.el-form-item__content) {
  display: flex !important;
  align-items: center !important;
  min-width: 0 !important;
  text-indent: 0 !important;
}

/* 确保数字输入框在 grid 中正确显示 */
.pro-form :deep(.el-form-item .el-input-number) {
  display: inline-flex !important;
}

/* Grid 布局时的按钮容器样式 */
.pro-form.grid-layout :deep(.el-form-item:last-child) {
  grid-column: span 2 !important;
  display: flex !important;
  justify-content: center !important;
  gap: 20px !important;
  margin-top: 20px !important;
}

.pro-form :deep(.el-form-item:last-child .el-button) {
  min-width: 100px !important;
}
</style>
