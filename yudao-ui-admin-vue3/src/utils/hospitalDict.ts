/**
 * 医院字典工具方法
 * 
 * 【为什么不用 getDictDataByType】
 * RuoYi-vue-pro 后端 DictDataController 没有 /type 端点，
 * 框架的设计是：登录时通过 /simple-list 一次性加载所有字典数据到 Pinia Store，
 * 然后前端通过 getDictOptions/getIntDictOptions/getDictLabel 从本地缓存读取。
 * 
 * 【为什么用 getIntDictOptions】
 * 后端字典值(value)是字符串类型(如 "0","1","2")，
 * 但我们数据库表的字段值是整数(0,1,2)。
 * getIntDictOptions 会自动把 value 转成 number，这样 el-select 的 v-model 绑定整数时能正确匹配。
 * 
 * 【用法示例】
 * const statusOptions = getIntDictOptions('hospital_doctor_status')
 * const statusLabel = getDictLabel('hospital_doctor_status', row.status)
 */
import { getIntDictOptions, getDictOptions, getDictLabel as ruoyiGetDictLabel } from '@/utils/dict'

/** 重新导出 RuoYi 自带的方法，保持接口一致 */
export { getIntDictOptions, getDictOptions }

/**
 * 字典值 → 显示文本
 * 
 * @param dictType 字典类型（如 hospital_doctor_status）
 * @param value 字典值（整数或字符串均可）
 * @returns 对应的中文标签
 */
export const getDictLabel = (dictType: string, value: any): string => {
  return ruoyiGetDictLabel(dictType, value)
}

/**
 * 字典值 → Element Plus Tag 颜色类型
 * 
 * @param dictType 字典类型
 * @param value 字典值
 * @returns el-tag 的 type 属性值（success/warning/danger/info 等）
 */
export const getDictColorType = (dictType: string, value: any): string => {
  const options = getDictOptions(dictType)
  const item = options.find((d: any) => String(d.value) === String(value))
  return item?.colorType || 'info'
}
