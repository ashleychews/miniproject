document.addEventListener('DOMContentLoaded', function () {
  const items = document.querySelectorAll('.image-wrapper');
  const itemCount = items.length;
  const nextItem = document.querySelector('.next');
  const previousItem = document.querySelector('.previous');
  let count = 0;

  function showItem(index) {
    items.forEach(item => item.classList.remove('active'));
    items[index].classList.add('active');
  }

  function showNextItem() {
    count = (count + 1) % itemCount;
    showItem(count);
  }

  function showPreviousItem() {
    count = (count - 1 + itemCount) % itemCount;
    showItem(count);
  }

  function keyPress(e) {
    e = e || window.event;
    if (e.keyCode == '37') {
      showPreviousItem();
    } else if (e.keyCode == '39') {
      showNextItem();
    }
  }

  nextItem.addEventListener('click', showNextItem);
  previousItem.addEventListener('click', showPreviousItem);
  document.addEventListener('keydown', keyPress);

  // Show the first item on page load
  showItem(count);
});