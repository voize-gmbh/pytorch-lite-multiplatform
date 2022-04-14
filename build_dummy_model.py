import torch
import torch.nn as nn

class DummyModel(nn.Module):
    def __init__(self):
        super().__init__()
        self.linear = nn.Linear(10, 10)

    def forward(self, x):
        return self.linear(x)

def main():
    model = DummyModel()
    model.eval()
    scripted_module = torch.jit.script(model)
    scripted_module._save_for_lite_interpreter("module.ptl")

if __name__ == "__main__":
    main()