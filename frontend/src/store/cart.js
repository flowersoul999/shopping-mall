/**
 * 购物车状态管理Store
 * 使用Pinia管理购物车列表、商品数量、总价等全局状态
 * 实现添加商品、更新数量、删除商品、清空购物车等功能
 */
import { defineStore } from 'pinia'                         // 引入Pinia的defineStore方法
import {
  getCartList as getCartListApi,
  addToCart as addToCartApi,
  updateCartItem as updateCartItemApi,
  deleteCartItem as deleteCartItemApi,
  clearCart as clearCartApi
} from '../api/cart'                                       // 引入购物车API
import { ElMessage } from 'element-plus'                    // 引入消息提示组件

/**
 * 创建购物车Store
 * @param {string} 'cart' - Store名称，用于Pinia内部标识
 * @param {Object} options - Store配置对象
 */
export const useCartStore = defineStore('cart', {
  /**
   * 状态定义
   * 存储购物车相关的数据
   */
  state: () => ({
    /**
     * 购物车商品列表
     * 每个商品包含：id、productId、productName、productImage、price、quantity、selected等字段
     */
    cartList: [],
    /**
     * 购物车商品总数量（所有商品数量之和）
     */
    cartCount: 0
  }),

  /**
   * 计算属性（Getters）
   * 根据state计算派生值，具有缓存特性
   */
  getters: {
    /**
     * 计算选中商品的总价格
     * @returns {number} 选中商品的总价
     */
    totalPrice: (state) => {
      return state.cartList
        .filter(item => item.selected !== false)            // 过滤出选中的商品
        .reduce((total, item) => total + (item.price || 0) * (item.quantity || 1), 0) // 计算总价
    },
    /**
     * 获取选中的商品列表
     * @returns {Array} 选中商品的数组
     */
    selectedItems: (state) => {
      return state.cartList.filter(item => item.selected !== false)
    }
  },

  /**
   * 动作方法（Actions）
   * 用于修改state、调用API、执行异步操作
   */
  actions: {
    /**
     * 获取购物车列表方法
     * 从后端加载购物车数据并更新状态
     */
    async fetchCartList() {
      try {
        // 调用获取购物车列表API
        const res = await getCartListApi()
        if (res.code === 200) {
          // 更新购物车列表
          this.cartList = res.data || []
          // 重新计算购物车商品总数
          this.cartCount = this.cartList.reduce((count, item) => count + (item.quantity || 1), 0)
        }
      } catch (error) {
        // 请求失败时清空购物车数据
        this.cartList = []
        this.cartCount = 0
      }
    },

    /**
     * 添加商品到购物车方法
     * @param {Object} product - 商品信息
     * @param {number} product.id - 商品ID（或product.productId）
     * @param {number} [product.quantity] - 添加数量，默认为1
     * @returns {boolean} true表示添加成功，false表示添加失败
     */
    async addToCart(product) {
      try {
        // 调用添加购物车API
        const res = await addToCartApi({
          productId: product.id || product.productId,        // 兼容不同字段名
          quantity: product.quantity || 1                   // 默认数量为1
        })
        if (res.code === 200) {
          // 显示添加成功提示
          ElMessage.success('已添加到购物车')
          // 重新加载购物车列表，确保数据最新
          await this.fetchCartList()
          return true
        }
        return false
      } catch (error) {
        // 添加失败，返回false
        return false
      }
    },

    /**
     * 更新购物车商品数量方法
     * @param {number} id - 购物车项ID
     * @param {number} quantity - 新的数量
     */
    async updateQuantity(id, quantity) {
      try {
        // 调用更新购物车项API
        const res = await updateCartItemApi(id, { quantity })
        if (res.code === 200) {
          // 找到对应的购物车项并更新数量
          const item = this.cartList.find(item => item.id === id)
          if (item) {
            item.quantity = quantity
          }
          // 重新计算购物车商品总数
          this.cartCount = this.cartList.reduce((count, item) => count + (item.quantity || 1), 0)
        }
      } catch (error) {
        // 更新失败时保持静默，不显示错误提示
      }
    },

    /**
     * 删除购物车商品方法
     * @param {number} id - 购物车项ID
     */
    async removeItem(id) {
      try {
        // 调用删除购物车项API
        const res = await deleteCartItemApi(id)
        if (res.code === 200) {
          // 从本地列表中移除该商品
          this.cartList = this.cartList.filter(item => item.id !== id)
          // 重新计算购物车商品总数
          this.cartCount = this.cartList.reduce((count, item) => count + (item.quantity || 1), 0)
          // 显示删除成功提示
          ElMessage.success('已删除购物项')
        }
      } catch (error) {
        // 删除失败时保持静默，不显示错误提示
      }
    },

    /**
     * 清空购物车方法
     */
    async clearCart() {
      try {
        // 调用清空购物车API
        const res = await clearCartApi()
        if (res.code === 200) {
          // 清空购物车列表
          this.cartList = []
          // 重置商品总数为0
          this.cartCount = 0
          // 显示清空成功提示
          ElMessage.success('购物车已清空')
        }
      } catch (error) {
        // 清空失败时保持静默，不显示错误提示
      }
    }
  }
})
