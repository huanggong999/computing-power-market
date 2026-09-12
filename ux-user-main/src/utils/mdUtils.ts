const directoryInit = () => {
  const anchors = previewRef.value.$el.querySelectorAll('h1,h2,h3,h4,h5,h6')
  const arr = Array.from(anchors).filter((title) => !!title.innerText.trim())
  if (!arr.length) {
    titles.value = []
    return
  }
  const hTags = Array.from(new Set(arr.map((title) => title.tagName))).sort()
  titles.value = arr.map((el) => ({
    id: 'directory-' + el.getAttribute('data-v-md-line'),
    title: el.innerText,
    lineIndex: el.getAttribute('data-v-md-line'),
    indent: hTags.indexOf(el.tagName),
    pixel: el.getBoundingClientRect().top - 60,
  }))
}
