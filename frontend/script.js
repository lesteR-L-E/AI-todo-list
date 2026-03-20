const API_URL = 'http://localhost:8080/todos';

const btnPost = document.querySelector('.btn--post');
const btnDeleteCompleted = document.querySelector('.btn--deleteCompleted');

const todoList = document.getElementById('todo-list');

///////////////////////////////////////////////////////////////

//渲染函数
function renderTodos(todos) {
  const list = document.getElementById('todo-list');
  list.innerHTML = '';

  todos.forEach(todo => {
    const li = document.createElement('li');

    li.innerHTML = `
    <span style="text-decoration: ${todo.completed ? 'line-through' : 'none'}">
        ${todo.title}
    </span>
    <button class = "btn--toggle" data-complete = "${todo.completed}" data-id ="${todo.id}">
        ${todo.completed ? '取消' : '完成'}
    </button>
    <button class = "btn--delete" data-id ="${todo.id}">删除</button>
    `;

    btnDelete = document.querySelectorAll('.btn--delete');

    list.appendChild(li);
  });
}

// 获取所有 todo
async function fetchTodos() {
  const response = await fetch(API_URL);
  const data = await response.json();
  renderTodos(data);
}

//增加任务
async function addTodoSingle() {
  const input = document.getElementById('todo-input');
  const title = input.value;

  if (!title) return;

  await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      title: title,
      completed: false,
      userId: 1,
    }),
  });

  input.value = '';
  fetchTodos(); // 重新加载列表
}

//删除任务
async function deleteTodo(id) {
  await fetch(`${API_URL}/${id}`, {
    method: 'DELETE',
  });

  fetchTodos(); // 删除后刷新
}

//删除已完成任务
async function deleteCompleteTodo() {
  const response = await fetch(`${API_URL}/completed`, {
    method: 'DELETE',
  });
  const data = await response.json();
  showToast(`delete ${data.deleted} completed todos`);
  fetchTodos(); // 删除后刷新
}

//更新任务
async function toggleTodo(id, completed) {
  await fetch(`${API_URL}/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      completed: !completed,
    }),
  });

  fetchTodos();
}

//提示函数
function showToast(message) {
  const toast = document.getElementById('toast');

  toast.innerText = message;
  toast.style.display = 'block';

  setTimeout(() => {
    toast.style.display = 'none';
    toast.innerText = '';
  }, 2000);
}

///////////////////////////////////////////////////////////////

// 页面加载时渲染当前的任务
fetchTodos();

//按添加按钮或回车键增加任务
btnPost.addEventListener('click', addTodoSingle);
document.addEventListener('keydown', function (e) {
  if (e.key === 'Enter') addTodoSingle();
});

//为所有任务增加监听
todoList.addEventListener('click', function (e) {
  e.preventDefault();
  const press = e.target;

  if (press.classList.contains('btn--delete')) {
    deleteTodo(Number(press.dataset.id));
  } else if (press.classList.contains('btn--toggle')) {
    toggleTodo(
      Number(press.dataset.id),
      press.dataset.complete === 'false' ? false : true,
    );
  }
});

//删除所有已完成任务
btnDeleteCompleted.addEventListener('click', deleteCompleteTodo);
