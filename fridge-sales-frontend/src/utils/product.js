export function parseProductName(name) {
  if (!name) return { brand: '', category: '', sku: '', displayName: '' }
  
  const parts = name.split(' ').filter(p => p.trim())
  
  if (parts.length >= 3) {
    const brand = parts[0] || ''
    const category = parts[1] || ''
    const sku = parts.slice(2).join(' ') || ''
    const displayName = `${brand} ${category}`
    return { brand, category, sku, displayName }
  }
  
  return { 
    brand: parts[0] || '', 
    category: parts[1] || '', 
    sku: '', 
    displayName: name 
  }
}

export function formatProductDisplay(product) {
  const parsed = parseProductName(product.name)
  return {
    ...product,
    parsedName: parsed,
    displayName: parsed.displayName,
    displaySku: parsed.sku
  }
}
