function setclass(id, state) {
    var element = document.getElementById(id);
    var list = element.classList;
    if (!state) {
        if (list.contains(id + "-off")) {
            return;
        }
        element.classList.add(id + "-off");
        element.classList.remove(id + "-on");
    } else {
        if (list.contains(id + "-on")) {
            return;
        }
        element.classList.add(id + "-on");
        element.classList.remove(id + "-off");
    }
}