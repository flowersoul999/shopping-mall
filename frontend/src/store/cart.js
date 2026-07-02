import { defineStore } from 'pinia'
import {
  getCartList as getCartListApi,
  addToCart as addToCartApi,
  updateCartItem as updateCartItemApi,
  deleteCartItem as deleteCartItemApi,
  clearCart as clearCartApi
} from '../api/cart'
import { ElMessage } from 'element-plus'

export const useCartStore = defineStore('cart', {
  state: () => ({
    cartList: [],
    cartCount: 0
  }),

  getters: {
    totalPrice: (state) => {
      return state.cartList
        .filter(item => item.selected !== false)
        .reduce((total, item) => total + (item.price || 0) * (item.quantity || 1), 0)
    },
    selectedItems: (state) => {
      return state.cartList.filter(item => item.selected !== false)
    }
  },

  actions: {
    async fetchCartList() {
      try {
        const res = await getCartListApi()
        if (res.code === 200) {
          this.cartList = res.data || []
          this.cartCount = this.cartList.reduce((count, item) => count + (item.quantity || 1), 0)
        }
      } catch (error) {
        this.cartList = []
        this.cartCount = 0
      }
    },

    async addToCart(product) {
      try {
        const res = await addToCartApi({
          productId: product.id || product.productId,
          quantity: product.quantity || 1
        })
        if (res.code === 200) {
          ElMessage.success('已添加到购物车')
          await this.fetchCartList()
          return true
        }
        return false
      } catch (error) {
        return false
      }
    },

    async updateQuantity(id, quantity) {
      try {
        const res = await updateCartItemApi(id, { quantity })
        if (res.code === 200) {
          const item = this.cartList.find(item => item.id === id)
          if (item) {
            item.quantity = quantity
          }
          this.cartCount = this.cartList.reduce((count, item) => count + (item.quantity || 1), 0)
        }
      } catch (error) {
        // Silent
      }
    },

    async removeItem(id) {
      try {
        const res = await deleteCartItemApi(id)
        if (res.code === 200) {
          this.cartList = this.cartList.filter(item => item.id !== id)
          this.cartCount = this.cartList.reduce((count, item) => count + (item.quantity || 1), 0)
          ElMessage.success('已删除购物项')
        }
      } catch (error) {
        // Silent
      }
    },

    async clearCart() {
      try {
        const res = await clearCartApi()
        if (res.code === 200) {
          this.cartList = []
          this.cartCount = 0
          ElMessage.success('购物车已清空')
        }
      } catch (error) {
        // Silent
      }
    }
  }
})
